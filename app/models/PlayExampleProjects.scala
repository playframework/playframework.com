package models

import com.google.inject._
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.libs.ws.WSClient

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

case class ExampleSource(relativePath: String, url: String)

object ExampleSource {
  implicit val reads = Json.reads[ExampleSource]
}

case class ExampleProject(
  displayName: String,
  templateName: String,
  gitHubRepo: String,
  gitHubUrl: String,
  keywords: Seq[String],
  downloadUrl: String,
  source: ExampleSource
) {
  val isPlay: Boolean = keywords contains "play"
  val playBranch: Option[String] = ExampleProject.AcceptablePlayBranches.find(keywords.contains)
}

object ExampleProject {
  val AcceptablePlayBranches = (0 to 5).map(v => s"2.$v.x").reverse

  implicit val reads = Json.reads[ExampleProject]
}

case class PlayExampleProjects(projects: Seq[ExampleProject] = Seq.empty) {
  lazy val byVersion: Seq[(String, Seq[ExampleProject])] = {
    (projects.groupBy(_.playBranch getOrElse "") - "").toSeq.sortBy(_._1).reverse
  }
}

object PlayExampleProjects {
  implicit val reads = Json.reads[PlayExampleProjects]
}

class ExamplesModule extends AbstractModule {
  override def configure() = {
    bind(classOf[PlayExampleProjects]).toProvider(classOf[PlayExampleProjectsProvider])
    bind(classOf[PlayExampleProjectsProvider]).asEagerSingleton()
  }
}

class PlayExampleProjectsProvider @Inject() (ws: WSClient)(implicit ec: ExecutionContext) extends Provider[PlayExampleProjects] {

  private val logger = Logger(getClass)

  override lazy val get = Await.result(getExampleProjects, 100.seconds)

  private def getExampleProjects: Future[PlayExampleProjects] = {
    ws.url("https://example.lightbend.com/v1/api/all-templates").get().map { result =>
      Json.fromJson[Seq[ExampleProject]](result.json) match {
        case JsSuccess(value, path) =>
          PlayExampleProjects(value.filter(_.isPlay))
        case err: JsError =>
          logger.error(s"Failed to parse JSON from example code service: ${err.errors}")
          throw new IllegalStateException("Could not parse JSON from example code service!")
      }
    }
  }
}
