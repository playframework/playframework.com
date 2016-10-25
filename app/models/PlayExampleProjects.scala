package models

import javax.inject.Inject

import com.google.inject.{AbstractModule, Singleton}
import play.api.Configuration
import play.api.cache.CacheApi
import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

case class ExampleProject(
  displayName: String,
  downloadUrl: String,
   gitHubRepo: String,
    gitHubUrl: String,
     keywords: Seq[String],
 templateName: String
)

object ExampleProject {
  implicit val format = Json.format[ExampleProject]
}

case class PlayExampleProjects(projects: Seq[ExampleProject],
                               private val service: PlayExampleProjectsService) {

  private def playVersion(exampleProject: ExampleProject): String = {
    exampleProject.keywords.find(k => service.validPlayVersions.contains(k)).get
  }

  lazy val byVersion: Seq[(String, Seq[ExampleProject])] = {
    projects.groupBy(playVersion).toSeq.sortBy(_._1).reverse
  }
}

class ExamplesModule extends AbstractModule {
  override def configure() = {
    bind(classOf[PlayExampleProjectsService]).in(classOf[Singleton])
  }
}

@Singleton
class PlayExampleProjectsService @Inject()(configuration: Configuration,
                                           ws: WSClient,
                                           cache: CacheApi)(implicit ec: ExecutionContext) {
  import scala.collection.JavaConverters._


  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  val templatesUrl = configuration.getString("examples.apiUrl").get

  val validPlayVersions: Set[String] = configuration.getStringList("examples.playVersions").get.asScala.toSet

  // remove this once filtering works in example service...
  def playProject(p: ExampleProject): Boolean = {
    p.keywords.contains("play") &&
      (p.keywords.toSet & validPlayVersions).nonEmpty
  }

  def examples(): Future[PlayExampleProjects] = {
    ws.url(templatesUrl).withQueryString("keyword" -> "play").get().map { r =>
      val json: JsValue = r.json
      Json.fromJson[Seq[ExampleProject]](json) match {
        case JsSuccess(allProjects, _) =>
          val playProjects = allProjects.filter(playProject)
          cache.set("example.projects", playProjects)
          PlayExampleProjects(projects = playProjects, this)
        case JsError(errors: Seq[(JsPath, Seq[ValidationError])]) =>
          logger.error(s"Cannot parse example projects\n${errors}")
          PlayExampleProjects(Seq.empty, this)
      }
    }
  }

  def cached(): Option[PlayExampleProjects] = {
    cache.get[PlayExampleProjects]("example.projects")
  }

  // preload the cache...
  examples()
}
