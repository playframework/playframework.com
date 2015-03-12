package actors

import actors.DocumentationActor.{UpdateDocumentation, DocumentationGitRepo, DocumentationGitRepos}
import akka.actor.{ActorRef, Actor}
import models.documentation._
import org.apache.commons.io.IOUtils
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.util.Base64
import play.api.i18n.{MessagesApi, Lang, Messages}
import play.doc.{PageIndex, PlayDoc}
import utils.{PlayGitRepository, AggregateFileRepository}
import scala.concurrent.duration._

object DocumentationPollingActor {
  case object Tick
}

/**
 * The documentation polling actor does asynchronous background polling of remote git repositories, as well as the
 * expensive task of scanning/indexing the repo to extract all the available versions and table of contents for the
 * documentation.
 *
 * @param repos
 * @param documentationActor
 */
class DocumentationPollingActor(repos: DocumentationGitRepos, documentationActor: ActorRef, messages: MessagesApi) extends Actor {

  import DocumentationPollingActor._
  import context.dispatcher

  // Initial scan of documentation
  scanAndSendDocumentation()

  val schedule = context.system.scheduler.schedule(10.minutes, 10.minutes, self, Tick)

  override def postStop() = {
    schedule.cancel()
  }

  def receive = {
    case Tick =>
      repos.default.repo.fetch()
      repos.translations.foreach(_.repo.fetch())
      scanAndSendDocumentation()
  }

  /**
   * Determining the master version of the repo means looking up the master branch, if found, loading a configured
   * master version file out of it, running a configured regular expression against that file to extract the version,
   * and if found, returning that version and the hash of the repo it comes from.
   */
  private def determineMasterVersion(repo: DocumentationGitRepo): Option[(Version, ObjectId)] = {
    def fileContents(hash: ObjectId, file: String): Option[String] = {
      repo.repo.loadFile(hash, file).map {
        case (size, is) => try {
          IOUtils.toString(is)
        } finally {
          is.close()
        }
      }
    }

    for {
      masterVersion <- repo.config.masterVersion
      masterHash <- repo.repo.hashForRef("master")
      contents <- fileContents(masterHash, masterVersion.file)
      matched <- masterVersion.pattern.findFirstMatchIn(contents)
      version <- Version.parse(matched.group(1).replace("-SNAPSHOT", ".x"))
    } yield version -> masterHash
  }

  private def scanAndSendDocumentation() {
    // First the default (English) translation

    // Find all the versions in the repo. The versions are all tags, and all branches that look like a version number.
    val defaultVersions = (parseVersionsFromRefs(repos.default.repo.allTags) ++ parseVersionsFromRefs(
      repos.default.repo.allBranches
        .filter(_._1.matches("""\d+\.\d+\.x"""))
    )).map(v => (v._1, v._2.name, repos.default.repo.fileRepoForHash(v._2), v._1.name))

    // Find the master version
    val defaultMasterVersion = determineMasterVersion(repos.default).flatMap {
      case (version, hash) if defaultVersions.forall(_._1 != version) =>
        val repo = repos.default.repo.fileRepoForHash(hash)
        Some((version, hash.name, repo, "master"))
      case _ => None
    }

    val allVersions = (defaultVersions ++ defaultMasterVersion).toList.sortBy(_._1).reverse.map {
      case (version, cacheId, repo, symName) =>
        implicit val lang = repos.default.config.lang

        TranslationVersion(version, repo,
          new PlayDoc(repo, repo, "resources", version.name,
            PageIndex.parseFrom(repo, messages("documentation.home"), Some("manual")),
            messages("documentation.next")
          ), xorHashes(cacheId, utils.SiteVersion.hash), symName
        )
    }

    val defaultTranslation = Translation(allVersions, repos.default.repo, repos.default.config.gitHubSource)

    // Now for each translation
    val translations = repos.translations.map { t =>

      // Parse all the versions from tags, branches and the master version
      val gitTags = parseVersionsFromRefs(t.repo.allTags).map(v => (v._1, v._2, v._1.name))
      val gitBranches = parseVersionsFromRefs(
        t.repo.allBranches
          .filter(_._1.matches("""\d+\.\d+\.x"""))
      ).map(v => (v._1, v._2, v._1.name))
      val masterVersion = determineMasterVersion(t).map(v => (v._1, v._2, "master"))

      implicit val lang = t.config.lang
      val versions = versionsToTranslations(t.repo, gitTags ++ gitBranches ++ masterVersion, defaultTranslation)

      t.config.lang -> Translation(versions, t.repo, t.config.gitHubSource)
    }.toMap

    val documentation = Documentation(defaultTranslation, repos.default.config.lang, translations)

    documentationActor ! UpdateDocumentation(documentation)
  }

  private def parseVersionsFromRefs(refs: Seq[(String, ObjectId)]): Seq[(Version, ObjectId)] = {
    refs.flatMap { ref =>
      Version.parse(ref._1).map(_ -> ref._2)
    }
  }

  private def versionsToTranslations(repo: PlayGitRepository, versions: Seq[(Version, ObjectId, String)],
                                     aggregate: Translation)(implicit lang: Lang): List[TranslationVersion] = {
    versions.sortBy(_._1).reverse.map { version =>
      val baseRepo = repo.fileRepoForHash(version._2)
      val aggregateVersion = aggregate.byVersion.get(version._1)
      val (fileRepo, cacheId) = aggregateVersion.fold(baseRepo -> xorHashes(version._2.name, utils.SiteVersion.hash)) { default =>

        new AggregateFileRepository(Seq(baseRepo, default.repo)) ->
          xorHashes(version._2.name, default.cacheId)
      }

      val playDoc = new PlayDoc(fileRepo, fileRepo, "resources", version._1.name,
        PageIndex.parseFrom(fileRepo, messages("documentation.home"), Some("manual")),
        messages("documentation.next")
      )

      TranslationVersion(version._1, fileRepo, playDoc, cacheId, version._3)
    }.toList
  }

  private def xorHashes(hash1: String, hash2: String): String = {
    val ba1 = Base64.decode(hash1)
    val ba2 = Base64.decode(hash2)
    val result = new Array[Byte](20)
    for (i <- 0 until 20) {
      result(i) = (ba1(i) ^ ba2(i)).asInstanceOf[Byte]
    }
    Base64.encodeBytes(result)
  }
}
