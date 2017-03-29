package actors

import java.io.File

import akka.actor.{ ActorSystem, ActorRef }
import javax.inject.{Provider, Inject, Singleton}
import com.google.inject.AbstractModule
import models.documentation._
import play.api._
import play.api.i18n.{MessagesApi, Lang}
import play.api.libs.concurrent.{AkkaGuiceSupport, Akka}
import scala.collection.JavaConversions._

class ActorsModule extends AbstractModule with AkkaGuiceSupport {
  def configure() = {
    bindActor[DocumentationActor]("documentation-actor")
    bindActorFactory[DocumentationPollingActor, DocumentationPollingActor.Factory]
    bind(classOf[DocumentationConfig]).toProvider(classOf[DocumentationConfigProvider])
  }
}

@Singleton
class DocumentationConfigProvider @Inject() (environment: Environment, configuration: Configuration) extends Provider[DocumentationConfig] {

  lazy val get = loadConfig.getOrElse(DocumentationConfig(
    TranslationConfig(Lang("en"), environment.rootPath, None, "origin", None, None), Nil))

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
