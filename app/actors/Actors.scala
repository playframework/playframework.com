package actors

import java.io.File

import com.google.inject.AbstractModule
import com.typesafe.config.Config
import javax.inject.{Inject, Provider, Singleton}
import models.documentation._
import play.api._
import play.api.i18n.Lang
import play.api.libs.concurrent.AkkaGuiceSupport

class ActorsModule extends AbstractModule with AkkaGuiceSupport {
  def configure() = {
    bindActor[DocumentationActor]("documentation-actor")
    bindActorFactory[DocumentationPollingActor, DocumentationPollingActor.Factory]
    bind(classOf[DocumentationConfig]).toProvider(classOf[DocumentationConfigProvider])
    bind(classOf[DocumentationRedirects]).toProvider(classOf[DocumentationRedirectsProvider])
  }
}

@Singleton
class DocumentationRedirectsProvider @Inject()(configuration: Configuration) extends Provider[DocumentationRedirects] {
  override def get: DocumentationRedirects = DocumentationRedirects(
    configuration.get[Seq[Config]]("documentation.redirects").map { config =>
      RedirectPage(
        from = config.getString("from"),
        to = config.getString("to")
      )
    }
  )
}

@Singleton
class DocumentationConfigProvider @Inject() (environment: Environment, configuration: Configuration) extends Provider[DocumentationConfig] {

  lazy val get: DocumentationConfig = loadConfig.getOrElse(DocumentationConfig(
    TranslationConfig(Lang("en"), environment.rootPath, None, "origin", None, None), Nil))

  private def loadConfig: Option[DocumentationConfig] = {
    for {
      docsConfig <- configuration.getOptional[Configuration]("documentation")
      path <- docsConfig.getOptional[String]("path").map(basePath)
      mainConfig <- docsConfig.getOptional[Configuration]("main")
      mainTranslation <- loadTranslationConfig(path, mainConfig)
      translations <- docsConfig.getOptional[Seq[Config]]("translations")
    } yield {
      DocumentationConfig(mainTranslation,
        translations.toList.collect(Function.unlift(loadTranslationConfig(path, _))))
    }
  }

  private def loadTranslationConfig(base: File, config: Config): Option[TranslationConfig] = {
    loadTranslationConfig(base, Configuration(config))
  }

  private def loadTranslationConfig(base: File, config: Configuration): Option[TranslationConfig] = {
    for {
      lang <- config.getOptional[String]("lang")
      repo <- config.getOptional[String]("repo") if verifyTranslationPath(base, repo, lang)
    } yield {
      TranslationConfig(
        Lang(lang),
        new File(base, repo).getCanonicalFile,
        config.getOptional[String]("path"),
        config.getOptional[String]("remote").getOrElse("origin"),
        for {
          file <- config.getOptional[String]("versionFile")
          pattern <- config.getOptional[String]("versionPattern")
        } yield MasterVersion(file, pattern.r),
        config.getOptional[String]("gitHubSource")
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
