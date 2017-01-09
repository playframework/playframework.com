package models

import javax.inject.Inject

import com.google.inject.{AbstractModule, Singleton}
import play.api.Configuration
import play.api.cache.CacheApi
import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.libs.ws.WSClient

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

case class TemplateParameter(
  `type`: String,
  query: String,
  displayName: String,
  required: Boolean,
  defaultValue: Option[String],
  pattern: Option[String],
  format: Option[String]
)

object TemplateParameter {
  implicit val format: Format[TemplateParameter] = Json.format
}

case class ExampleProject(
  displayName: String,
  downloadUrl: String,
  gitHubRepo: String,
  gitHubUrl: String,
  keywords: Seq[String],
  templateName: String,
  parameters: Option[Seq[TemplateParameter]]
) {
  def hasParams: Boolean = parameters.nonEmpty
  def params: Seq[TemplateParameter] = parameters.toSeq.flatten
}

object ExampleProject {
  implicit val format: Format[ExampleProject] = Json.format
}

class ExamplesModule extends AbstractModule {
  override def configure() = {
    bind(classOf[PlayExampleProjectsService]).asEagerSingleton()
  }
}

@Singleton
class PlayExampleProjectsService @Inject()(
  configuration: Configuration,
  ws: WSClient,
  cache: CacheApi
)(implicit ec: ExecutionContext) {

  val validPlayVersions: Seq[String] = configuration.get[Seq[String]]("examples.playVersions")

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  private val examplesUrl = configuration.get[String]("examples.apiUrl")

  // NOTE: TTL is really just a safety measure here.
  // We should re-deploy when we make major changes to projects
  private val examplesCacheTtl = configuration.getMillis("examples.cache.ttl").milliseconds

  private def playQueryString(version: String): Seq[(String, String)] = {
    Seq("keyword" -> "play", "keyword" -> version)
  }

  private def cacheKey(version: String): String = s"example.projects.$version"

  private def convertExampleProjects(version: String, json: JsValue): Seq[ExampleProject] = {
    Json.fromJson[Seq[ExampleProject]](json) match {
      case JsSuccess(allProjects, _) =>
        val playProjects = allProjects
        if (examplesCacheTtl.length > 0) {
          cache.set(cacheKey(version), playProjects, examplesCacheTtl)
        }
        playProjects
      case JsError(errors: Seq[(JsPath, Seq[ValidationError])]) =>
        logger.error(s"Cannot parse example projects for $version\n$errors")
        Seq.empty
    }
  }

  def examples(): Future[Seq[ExampleProject]] = {
    Future.sequence(validPlayVersions.map { version =>
      ws.url(examplesUrl).withQueryString(playQueryString(version): _*).get()
        .map(response => (version, response.json))
    }).map { response =>
      response.flatMap((convertExampleProjects _).tupled)
    }
  }

  def cached(): Option[Seq[ExampleProject]] = {
    validPlayVersions.foldLeft(Option(Seq.empty[ExampleProject])) { (result, version) =>
      result.fold(result) { projects =>
        cache.get[Seq[ExampleProject]](cacheKey(version)).map(projects ++ _)
      }
    }
  }

  // preload the cache...
  examples()
}