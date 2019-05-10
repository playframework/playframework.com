package controllers

import java.io.InputStream

import javax.inject.Inject
import javax.inject.Singleton
import models._
import org.apache.commons.io.IOUtils
import play.api._
import play.api.cache.SyncCacheApi
import play.api.i18n.I18nSupport
import play.api.i18n.Lang
import play.api.mvc._
import play.twirl.api.Html
import utils.Markdown
import views._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton
class Application @Inject()(
    environment: Environment,
    configuration: Configuration,
    releases: PlayReleases,
    exampleProjectsService: PlayExampleProjectsService,
    components: ControllerComponents,
    cacheApi: SyncCacheApi,
)(implicit ec: ExecutionContext, val reverseRouter: documentation.ReverseRouter)
    extends AbstractController(components)
    with Common
    with I18nSupport {

  private val VulnerableVersions = Set(
    "2.0",
    "2.0.1",
    "2.0.2",
    "2.0.3",
    "2.0.4",
    "2.0.5",
    "2.1",
    "2.1.1",
    "2.1.2",
  )

  private def news(version: Option[String]): Seq[Html] = {
    val message = version
      .filter(VulnerableVersions)
      .map { _ =>
        s"""<p class="vulnerability-warning">You are using a version of Play Framework that has a
        <a href="${routes.Security.vulnerability("20130806-SessionInjection")}">known vulnerability</a>.</p>
          <p>Please upgrade to a later version <a href="${routes.Application.download()}">here</a>.</p>"""

      }
      .orElse {
        if (!version.contains(releases.latest.version)) {
          Some(s"""Play framework ${releases.latest.version} is out!  Check it out <a href="${routes.Application
            .download()}">here</a>.""")
        } else {
          None
        }
      }
    message.toSeq.map(Html.apply)
  }

  def index = Action { implicit request =>
    Ok(html.index(releases))
  }

  def widget(version: Option[String]) = Action { request =>
    Ok(views.html.widget(news(version)))
  }

  // This used to be the download/getting-started page. We are keeping
  // the URL for SEO purposes only.
  def download = Action { implicit request =>
    MovedPermanently(routes.Application.gettingStarted.path)
  }

  def gettingStarted = Action.async { implicit request =>
    exampleProjectsService.cached() match {
      case Some(cached) =>
        val examples = toExamples(cached)
        Future.successful {
          Ok(html.gettingStarted(examples))
        }
      case None =>
        exampleProjectsService.examples().map { live =>
          val examples = toExamples(live)
          Ok(html.gettingStarted(examples))
        }
    }
  }

  def allreleases(platform: Option[String] = None) = Action { implicit request =>
    val selectedPlatform = Platform(platform.orElse(request.headers.get("User-Agent")))
    Ok(html.allreleases(releases, selectedPlatform))
  }

  def changelog =
    markdownAction("public/markdown/changelog.md", { implicit request =>
      views.html.changelog(_)
    })

  def conduct =
    markdownAction("public/markdown/code-of-conduct.md", { implicit request => markdown =>
      views.html.markdownPage("Code of conduct", markdown)
    })

  def communityProcess =
    markdownAction("public/markdown/community-process.md", { implicit request => markdown =>
      views.html.markdownPage("Community process", markdown)
    })

  def contributing =
    markdownAction("public/markdown/contributing.md", { implicit request => markdown =>
      views.html.markdownPage("Contributing", markdown)
    })

  def markdownAction(markdownFile: String, template: RequestHeader => Html => Html) = Action {
    implicit request =>
      def readInputStream(is: InputStream): String =
        try {
          IOUtils.toString(is, "utf-8")
        } finally {
          is.close()
        }

      def fromMarkdownToHtml(md: String): String = Markdown.toHtml(md, link => (link, link))

      // Read from cache or either from an input stream.
      val page = cacheApi.get[String](markdownFile).orElse {
        environment
          .resourceAsStream(markdownFile)
          .map(readInputStream)
          .map(fromMarkdownToHtml)
      }

      // We can cache the generated HTML like forever since
      // the file is updated only when deploying and cache
      // is gone when deploying.
      page.foreach(cacheApi.set(markdownFile, _))

      page match {
        case Some(content) => Ok(template(request)(Html(content))).withHeaders(CACHE_CONTROL -> "max-age=10000")
        case None          => notFound
      }
  }

  def support = Action { implicit request =>
    Ok(html.support())
  }

  def getInvolved = Action { implicit request =>
    Ok(html.getInvolved())
  }

  def cookie = Action { implicit request =>
    Ok(html.cookie())
  }

  // Deprecated links
  def movedTo(url: String, originalPath: String) = Action {
    MovedPermanently(url)
  }

  def onHandlerNotFound(route: String) = Action { implicit request =>
    if (route.endsWith("/")) {
      MovedPermanently("/" + request.path.take(request.path.length - 1).dropWhile(_ == '/'))
    } else {
      notFound
    }
  }

  def setPreferedLanguage(lang: String, path: String) = Action {
    if (path.startsWith("/")) {
      messagesApi.setLang(Redirect(path, SEE_OTHER), Lang(lang))
    } else {
      BadRequest
    }
  }

  def robots = Action {
    Ok("Sitemap: https://www.playframework.com/sitemap-index.xml")
  }

  // Set up the presentation object
  private def toExamples(projects: Seq[ExampleProject]): PlayExamples = {
    def playVersion(exampleProject: ExampleProject): String = {
      exampleProject.keywords.find(k => exampleProjectsService.validPlayVersions.contains(k)).get
    }

    def byVersion: Seq[(String, Seq[ExampleProject])] = {
      projects.groupBy(playVersion).toSeq.sortBy(_._1).reverse
    }

    def byLanguage(exampleProject: ExampleProject): String = {
      exampleProject.keywords match {
        case javaKeywords if javaKeywords.contains("java") =>
          "java"
        case scala =>
          "scala"
      }
    }

    val sections = byVersion.map {
      case (v, p) =>
        val TutorialKeyword = "tutorial" // this MUST be in the project keywords for this to work
        val SeedKeyword     = "seed"
        val tutorials = p
          .filter(e => e.keywords.contains(TutorialKeyword) && !e.hasParams)
          .groupBy(byLanguage)
          .mapValues(_.sortBy(_.displayName))

        val examples = p
          .filter(e => !e.keywords.contains(TutorialKeyword) && !e.hasParams)
          .groupBy(byLanguage)
          .mapValues(_.sortBy(_.displayName))

        val seeds = p
          .filter(e => e.hasParams && e.keywords.contains(SeedKeyword))
          .groupBy(byLanguage)
          .mapValues(_.sortBy(_.displayName))

        v -> PlayExampleSection(tutorials, seeds, examples)
    }
    PlayExamples(sections)
  }
}

case class PlayExamples(sections: Seq[(String, PlayExampleSection)])

case class PlayExampleSection(
    tutorials: Map[String, Seq[ExampleProject]],
    seeds: Map[String, Seq[ExampleProject]],
    examples: Map[String, Seq[ExampleProject]],
)
