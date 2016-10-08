package models

import com.google.inject.{Singleton, Provides, AbstractModule}
import org.apache.commons.io.IOUtils
import play.api.Environment
import play.api.libs.json.Json

case class ExampleProject(
  name: String,
  playVersion: String,
  githubUrl: String,
  downloadUrl: String
)

object ExampleProject {
  implicit val format = Json.format[ExampleProject]
}

case class PlayExampleProjects(projects: Seq[ExampleProject] = Seq.empty) {
  lazy val byVersion: Seq[(String, Seq[ExampleProject])] = {
    projects.groupBy(_.playVersion).toSeq.sortBy(_._1).reverse
  }
}

object PlayExampleProjects {
  implicit val format = Json.format[PlayExampleProjects]
}

class ExamplesModule extends AbstractModule {
  override def configure() = ()

  // TODO something nicer here
  @Provides @Singleton def examples(environment: Environment): PlayExampleProjects = {
    // TODO: this will eventually be replaced by an API from the example code service
    environment.resourceAsStream("playExamples.json").flatMap { is =>
      try {
        Json.fromJson[PlayExampleProjects](Json.parse(IOUtils.toByteArray(is))).asOpt
      } finally {
        is.close()
      }
    }.getOrElse(PlayExampleProjects())
  }
}
