package controllers

import javax.inject.{ Inject, Singleton }
import play.api._
import play.api.i18n.Lang
import play.api.mvc._
import views._
import play.twirl.api.Html
import utils.Markdown
import org.apache.commons.io.IOUtils
import play.api.libs.json.Json
import models._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.cache.Cache
import play.api.libs.ws.WS
import play.api.Play.current
import scala.util.Try

object Application extends Controller with Common {

  private def instance: Application = Play.current.injector.instanceOf[Application]

  def index = instance.index

  def widget(version: Option[String]) = instance.widget(version)

  def download(platform: Option[String] = None) = instance.download(platform)

  def changelog = instance.changelog

  def support = instance.support

  def getInvolved = instance.getInvolved

  // Deprecated links
  def movedTo(url: String, originalPath: String) = instance.movedTo(url, originalPath)

  def onHandlerNotFound(route: String) = instance.onHandlerNotFound(route)

  def setPreferedLanguage(lang: String) = instance.setPreferedLanguage(lang)

}

@Singleton
class Application @Inject() () extends Controller with Common {

  private lazy val releases: PlayReleases = {
    Play.maybeApplication.flatMap(app => Option(app.classloader.getResourceAsStream("playReleases.json"))).flatMap { is =>
      try {
        Json.fromJson[PlayReleases](Json.parse(IOUtils.toByteArray(is))).asOpt
      } finally {
        is.close()
      }
    }.getOrElse(PlayReleases(PlayRelease("unknown", None, Some("unknown"), None), Nil, Nil))
  }

  private val VulnerableVersions = Set(
    "2.0", "2.0.1", "2.0.2", "2.0.3", "2.0.4", "2.0.5",
    "2.1", "2.1.1", "2.1.2"
  )

  private def news(version: Option[String]): Seq[Html] = {
    val message = version.filter(VulnerableVersions).map { _ =>

      s"""<p style="font-weight: bold; color: red;">You are using a version of Play Framework that has a
        <a style="color: red;" href="${routes.Security.vulnerability("20130806-SessionInjection")}">known vulnerability</a>.</p>
          <p>Please upgrade to a later version <a href="${routes.Application.download()}">here</a>.</p>"""

    } orElse {
      if (version.forall(_ != releases.latest.version)) {
        Some(s"""Play framework ${releases.latest.version} is out!  Check it out <a href="${routes.Application.download()}">here</a>.""")
      } else {
        None
      }
    }
    message.toSeq.map(Html.apply)
  }


  def index = Action.async { implicit request =>
    latestActivator.map { activator =>
      Ok(html.index(activator))
    }
  }

  def widget(version: Option[String]) = Action { request =>
    Ok(views.html.widget(news(version)))
  }

  def download(platform: Option[String] = None) = Action.async { implicit request =>
    val selectedPlatform = Platform(platform.orElse(request.headers.get("User-Agent")))

    latestActivator.map { activator =>
      Ok(html.download(releases, activator, selectedPlatform))
    }
  }

  private def latestActivator: Future[ActivatorRelease] = {
    Cache.getAs[ActivatorRelease]("latest-activator").map(Future.successful).getOrElse {
      // cache miss
      play.api.Logger.info("latest activator version cache miss")
      Play.configuration.getString("activator.latest-url").map { url =>
        WS.url(url).withRequestTimeout(2000).get().map { response =>
          response.json.as[ActivatorRelease]
        } recover {
          case e =>
            play.api.Logger.error(s"Failed to get Activator version info ${e.getClass.getName}: ${e.getMessage}")
            Cache.getAs[ActivatorRelease]("latest-activator-eternal")
              .getOrElse(defaultActivatorLatest)
        }
      } getOrElse Future.successful(defaultActivatorLatest) andThen {
        case r: Try[ActivatorRelease] =>
          Cache.set("latest-activator", r.get, 60 * 10) // 10 minute cache timeout
          Cache.set("latest-activator-eternal", r.get, 0) // eternal in case activator service is down
      }
    }
  }

  // this should only happen if we have NEVER succeeded in getting
  // the activator info since our last restart
  private val defaultActivatorLatest: ActivatorRelease = ActivatorRelease(
    version = "(unknown)",
    url = "https://typesafe.com/platform/getstarted",
    miniUrl = "https://typesafe.com/platform/getstarted",
    size = "???M",
    miniSize = "?M",
    akkaVersion = "(unknown)",
    playVersion = "(unknown)",
    scalaVersion = "(unknown)")

  def changelog = Action { implicit request =>
    Play.maybeApplication.flatMap(app => Option(app.classloader.getResourceAsStream("public/markdown/changelog.md"))).map { is =>
      try {
        Ok(views.html.changelog(Html(Markdown.toHtml(IOUtils.toString(is), link => (link, link)))))
          .withHeaders(CACHE_CONTROL -> "max-age=10000")
      } finally {
        is.close()
      }
    } getOrElse notFound
  }

  def support = Action { implicit request =>
    Ok(html.support())
  }

  def getInvolved = Action { implicit request =>
    Ok(html.getInvolved())
  }

  // Deprecated links
  def movedTo(url: String, originalPath: String) = Action {
    MovedPermanently(url)
  }

  def onHandlerNotFound(route: String) = Action { implicit request =>
    if (route.endsWith("/")) {
      MovedPermanently(request.path.take(request.path.length - 1))
    } else {
      notFound
    }
  }

  def setPreferedLanguage(lang: String) = Action {
    Ok.withLang(Lang(lang))
  }

}
