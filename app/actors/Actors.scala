package actors

import java.io.File

import akka.actor.{ ActorSystem, ActorRef }
import javax.inject.{ Inject, Singleton }
import models.documentation._
import play.api._
import play.api.i18n.Lang
import play.api.libs.concurrent.Akka
import scala.collection.JavaConversions._

object Actors {
  def documentationActor(implicit app: Application): Option[ActorRef] = actors.documentationActor

  private def actors(implicit app: Application) = app.injector.instanceOf[Actors]
}

@Singleton
class Actors @Inject() (
  environment: Environment,
  configuration: Configuration,
  actorSystem: ActorSystem) {

  val documentationActor: Option[ActorRef] = loadConfig.map(config => actorSystem.actorOf(DocumentationActor.props(config)))

  private def loadConfig: Option[DocumentationConfig] = {
    for {
      docsConfig <- configuration.getConfig("documentation")
      path <- docsConfig.getString("path").map(basePath)
      mainConfig <- docsConfig.getConfig("main")
      mainTranslation <- loadTranslationConfig(path, mainConfig)
      translations <- docsConfig.getConfigList("translations")
    } yield {
      DocumentationConfig(mainTranslation,
        translations.toList.collect(Function.unlift(loadTranslationConfig(path, _))))
    }
  }

  private def loadTranslationConfig(base: File, config: Configuration): Option[TranslationConfig] = {
    for {
      lang <- config.getString("lang")
      repo <- config.getString("repo") if verifyTranslationPath(base, repo, lang)
    } yield {
      TranslationConfig(
        Lang(lang),
        new File(base, repo).getCanonicalFile,
        config.getString("path"),
        config.getString("remote").getOrElse("origin"),
        for {
          file <- config.getString("versionFile")
          pattern <- config.getString("versionPattern")
        } yield MasterVersion(file, pattern.r),
        config.getString("gitHubSource")
      )
    }
  }

  private def basePath(base: String) = {
    if (base == ".") {
      new File(environment.rootPath, "data")
    } else {
      new File(base)
    }
  }

  private def verifyTranslationPath(base: File, path: String, name: String) = {
    val translationPath = new File(base, path)
    if (translationPath.exists()) {
      true
    } else {
      Logger.warn("Not loading translation: " + name + " because its configured repo " + translationPath.getCanonicalPath + " doesn't exist")
      false
    }
  }

}