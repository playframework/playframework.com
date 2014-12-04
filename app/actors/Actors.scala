package actors

import java.io.File

import akka.actor.ActorRef
import models.documentation._
import play.api._
import play.api.i18n.Lang
import play.api.libs.concurrent.Akka
import scala.collection.JavaConversions._

object Actors {
  def documentationActor(implicit app: Application): Option[ActorRef] = actorsPlugin.flatMap(_.documentationActor)

  def actorsPlugin(implicit app: Application) = app.plugin[ActorsPlugin]
}

class ActorsPlugin(app: Application) extends Plugin {

  lazy val documentationActor = loadConfig.map(config => Akka.system(app).actorOf(DocumentationActor.props(config)))

  def loadConfig: Option[DocumentationConfig] = {
    for {
      docsConfig <- app.configuration.getConfig("documentation")
      path <- docsConfig.getString("path").map(basePath)
      mainConfig <- docsConfig.getConfig("main")
      mainTranslation <- loadTranslationConfig(path, mainConfig)
      translations <- docsConfig.getConfigList("translations")
    } yield {
      DocumentationConfig(mainTranslation,
        translations.toList.collect(Function.unlift(loadTranslationConfig(path, _))))
    }
  }

  def loadTranslationConfig(base: File, config: Configuration): Option[TranslationConfig] = {
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

  def basePath(base: String) = {
    if (base == ".") {
      new File(app.path, "data")
    } else {
      new File(base)
    }
  }

  def verifyTranslationPath(base: File, path: String, name: String) = {
    val translationPath = new File(base, path)
    if (translationPath.exists()) {
      true
    } else {
      Logger.warn("Not loading translation: " + name + " because it's configured repo " + translationPath.getCanonicalPath + " doesn't exist")
      false
    }
  }

  override def onStart() = {
    // Ensure the documentation actor is eagerly started
    documentationActor
  }
}