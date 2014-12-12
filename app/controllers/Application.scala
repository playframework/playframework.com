package controllers

import javax.inject.{ Inject, Singleton }
import play.api._
import play.api.i18n.{ Lang, MessagesApi }
import play.api.mvc._
import views._
import play.twirl.api.Html
import utils.Markdown
import org.apache.commons.io.IOUtils
import play.api.libs.json.Json
import models._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import play.api.cache.CacheApi
import play.api.libs.ws.WSClient
import scala.util.Try

@Singleton
class Application @Inject() (
  cache: CacheApi,
  environment: Environment,
  configuration: Configuration,
  environment: Environment,
  messages: MessagesApi,
  ws: WSClient) extends Controller with Common {

  private lazy val releases: PlayReleases = {
    environment.resourceAsStream("playReleases.json").flatMap { is =>
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
    cache.get[ActivatorRelease]("latest-activator").map(Future.successful).getOrElse {
      // cache miss
      play.api.Logger.info("latest activator version cache miss")
      configuration.getString("activator.latest-url").map { url =>
        ws.url(url).withRequestTimeout(2000).get().map { response =>
          response.json.as[ActivatorRelease]
        } recover {
          case e =>
            play.api.Logger.error(s"Failed to get Activator version info ${e.getClass.getName}: ${e.getMessage}")
            cache.get[ActivatorRelease]("latest-activator-eternal")
              .getOrElse(defaultActivatorLatest)
        }
      } getOrElse Future.successful(defaultActivatorLatest) andThen {
        case r: Try[ActivatorRelease] =>
          cache.set("latest-activator", r.get, 10 minutes) // 10 minute cache timeout
          cache.set("latest-activator-eternal", r.get, Duration.Inf) // eternal in case activator service is down
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

  def changelog = markdownAction("public/markdown/changelog.md", { implicit request =>
    views.html.changelog(_)
  })
  
  def conduct = markdownAction("public/markdown/code-of-conduct.md", { implicit request =>
    views.html.conduct(_)
  })

  def markdownAction(markdownFile: String, template: RequestHeader => Html => Html) = Action { implicit request =>
    environment.resourceAsStream(markdownFile).map { is =>
      try {
        Ok(template(request)(Html(Markdown.toHtml(IOUtils.toString(is), link => (link, link)))))
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
    messages.setLang(Ok, Lang(lang))
  }

}
