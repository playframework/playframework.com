package actors

import actors.DocumentationActor.DocumentationGitRepo
import actors.DocumentationActor.DocumentationGitRepos
import actors.DocumentationActor.UpdateDocumentation
import akka.actor.typed.{ ActorRef, Behavior }
import akka.actor.typed.scaladsl.{ ActorContext, Behaviors, LoggerOps }
import models.documentation._
import org.apache.commons.io.IOUtils
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.util.Base64
import play.api.i18n.Lang
import play.api.i18n.MessagesApi
import play.doc.PageIndex
import play.doc.PlayDoc
import play.doc.TranslatedPlayDocTemplates
import utils.AggregateFileRepository
import utils.PlayGitRepository

import scala.concurrent.duration._

object DocumentationPollingActor {
  case object Tick

  /**
   * Factory for creating the documentation polling actor
   */
  trait Factory {
    def apply(
        repos: DocumentationGitRepos,
        documentationActor: ActorRef[DocumentationActor.Command],
    ): Behavior[Tick.type]
  }

  def apply(
      messages: MessagesApi,
      repos: DocumentationGitRepos,
      documentationActor: ActorRef[DocumentationActor.Command],
  ): Behavior[Tick.type] = Behaviors.setup { context =>
    Behaviors.withTimers { timers =>
      timers.startTimerWithFixedDelay(Tick, Tick, 10.minutes) // no 1.minute initial delay
      new DocumentationPollingActor(messages, repos, documentationActor, context).initialBehavior
    }
  }
}

/**
 * The documentation polling actor does asynchronous background polling of remote git repositories, as well as the
 * expensive task of scanning/indexing the repo to extract all the available versions and table of contents for the
 * documentation.
 */
class DocumentationPollingActor(
    messages: MessagesApi,
    repos: DocumentationGitRepos,
    documentationActor: ActorRef[DocumentationActor.Command],
    context: ActorContext[DocumentationPollingActor.Tick.type],
) {
  import DocumentationPollingActor.Tick
  import context.log

  // Initial scan of documentation
  val initialBehavior = update(scanAndSendDocumentation(None))

  def update(old: Documentation): Behavior[Tick.type] = Behaviors.receiveMessage {
    case Tick =>
      repos.default.repo.fetch()
      repos.translations.foreach(_.repo.fetch())
      update(scanAndSendDocumentation(Some(old)))
  }

  /**
   * Determining the main version of the repo means looking up the main branch, if found, loading a configured
   * main version file out of it, running a configured regular expression against that file to extract the version,
   * and if found, returning that version and the hash of the repo it comes from.
   */
  private def determineMainVersion(repo: DocumentationGitRepo): Option[(Version, ObjectId)] = {
    def fileContents(hash: ObjectId, file: String): Option[String] = {
      repo.repo.loadFile(hash, file).map {
        case (size, is) =>
          try {
            IOUtils.toString(is, "utf-8")
          } finally {
            is.close()
          }
      }
    }

    for {
      mainVersion <- repo.config.mainVersion
      mainHash    <- repo.repo.hashForRef("main")
      contents      <- fileContents(mainHash, mainVersion.file)
      matched       <- mainVersion.pattern.findFirstMatchIn(contents)
      version       <- Version.parse(matched.group(1).replace("-SNAPSHOT", ".x"))
    } yield version -> mainHash
  }

  private def scanAndSendDocumentation(old: Option[Documentation]): Documentation = {
    // First the default (English) translation

    // Find all the versions in the repo. The versions are all tags, and all branches that look like a version number.
    val defaultVersions = (parseVersionsFromRefs(repos.default.repo.allTags) ++ parseVersionsFromRefs(
      repos.default.repo.allBranches
        .filter(_._1.matches("""\d+\.\d+\.x""")),
    )).map(v => (v._1, v._2.name, repos.default.repo.fileRepoForHash(v._2), v._1.name))

    // Find the main version
    val defaultMainVersion = determineMainVersion(repos.default).flatMap {
      case (version, hash) if defaultVersions.forall(_._1 != version) =>
        val repo = repos.default.repo.fileRepoForHash(hash)
        Some((version, hash.name, repo, "main"))
      case _ => None
    }

    val allVersions = (defaultVersions ++ defaultMainVersion).toList.sortBy(_._1).reverse.map {
      case (version, cacheId, repo, symName) =>
        val newCacheId = xorHashes(cacheId, utils.SiteVersion.hash)

        old.flatMap(_.default.byVersion.get(version)) match {
          // The version hasn't changed, don't rescan
          case Some(same: TranslationVersion) if same.cacheId == newCacheId => same
          case _ =>
            implicit val lang = repos.default.config.lang

            if (old.isDefined) {
              log.info2("Updating default documentation for {}: {}", version, cacheId)
            }

            val playDoc = new PlayDoc(
              markdownRepository = repo,
              codeRepository = repo,
              resources = "resources",
              playVersion = version.name,
              pageIndex = PageIndex.parseFrom(repo, messages("documentation.home"), Some("manual")),
              templates = new TranslatedPlayDocTemplates(messages("documentation.next")),
              pageExtension = None,
            )
            TranslationVersion(version, repo, playDoc, newCacheId, symName)
        }
    }

    val defaultTranslation = Translation(allVersions, repos.default.repo, repos.default.config.gitHubSource)

    // Now for each translation
    val translations = repos.translations.map { t =>
      // Parse all the versions from tags, branches and the main version
      val gitTags = parseVersionsFromRefs(t.repo.allTags).map(v => (v._1, v._2, v._1.name))
      val gitBranches = parseVersionsFromRefs(
        t.repo.allBranches
          .filter(_._1.matches("""\d+\.\d+\.x""")),
      ).map(v => (v._1, v._2, v._1.name))
      val mainVersion = determineMainVersion(t).map(v => (v._1, v._2, "main"))

      implicit val lang = t.config.lang
      val versions = versionsToTranslations(
        t.repo,
        gitTags ++ gitBranches ++ mainVersion,
        defaultTranslation,
        old.flatMap(_.translations.get(lang)),
      )

      t.config.lang -> Translation(versions, t.repo, t.config.gitHubSource)
    }.toMap

    val documentation = Documentation(defaultTranslation, repos.default.config.lang, translations)

    documentationActor ! UpdateDocumentation(documentation)
    documentation
  }

  private def parseVersionsFromRefs(refs: Seq[(String, ObjectId)]): Seq[(Version, ObjectId)] = {
    refs.flatMap { ref =>
      Version.parse(ref._1).map(_ -> ref._2)
    }
  }

  private def versionsToTranslations(
      repo: PlayGitRepository,
      versions: Seq[(Version, ObjectId, String)],
      aggregate: Translation,
      old: Option[Translation],
  )(implicit lang: Lang): List[TranslationVersion] = {
    versions
      .sortBy(_._1)
      .reverse
      .map { version =>
        val baseRepo         = repo.fileRepoForHash(version._2)
        val aggregateVersion = aggregate.byVersion.get(version._1)
        val (fileRepo, cacheId) =
          aggregateVersion.fold(baseRepo -> xorHashes(version._2.name, utils.SiteVersion.hash)) { default =>
            new AggregateFileRepository(Seq(baseRepo, default.repo)) ->
              xorHashes(version._2.name, default.cacheId)
          }

        old.flatMap(_.byVersion.get(version._1)) match {
          // The version hasn't changed, don't rescan
          case Some(same: TranslationVersion) if same.cacheId == cacheId => same
          case _ =>
            val playDoc = new PlayDoc(
              markdownRepository = fileRepo,
              codeRepository = fileRepo,
              resources = "resources",
              playVersion = version._1.name,
              pageIndex = PageIndex.parseFrom(fileRepo, messages("documentation.home"), Some("manual")),
              templates = new TranslatedPlayDocTemplates(messages("documentation.next")),
              pageExtension = None,
            )
            TranslationVersion(version._1, fileRepo, playDoc, cacheId, version._3)
        }

      }
      .toList
  }

  private def xorHashes(hash1: String, hash2: String): String = {
    val ba1    = Base64.decode(hash1)
    val ba2    = Base64.decode(hash2)
    val result = new Array[Byte](20)
    for (i <- 0 until 20) {
      result(i) = (ba1(i) ^ ba2(i)).asInstanceOf[Byte]
    }
    Base64.encodeBytes(result)
  }
}
