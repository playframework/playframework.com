package controllers

import javax.inject.{Named, Inject, Singleton}
import actors.ActivatorReleaseActor
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import models.certification.Certification
import play.api._
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.mvc._
import services.certification.CertificationDao
import views._
import play.twirl.api.Html
import utils.Markdown
import org.apache.commons.io.IOUtils
import models._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

@Singleton
class Application @Inject() (
  environment: Environment,
  configuration: Configuration,
  val messagesApi: MessagesApi,
  certificationDao: CertificationDao,
  @Named("activator-release-actor") activatorReleaseActor: ActorRef,
  releases: PlayReleases,
  exampleProjectsService: PlayExampleProjectsService
)(implicit ec: ExecutionContext) extends Controller with Common with I18nSupport {

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

  def index = Action { implicit request =>
    Ok(html.index(releases))
  }

  def widget(version: Option[String]) = Action { request =>
    Ok(views.html.widget(news(version)))
  }

  def download(platform: Option[String] = None) = Action.async { implicit request =>
    val selectedPlatform = Platform(platform.orElse(request.headers.get("User-Agent")))

    latestActivator.flatMap { activator =>
      exampleProjectsService.cached() match {
        case Some(cached) =>
          val examples = toExamples(cached)
          Future.successful {
            Ok(html.download(releases, examples, activator, selectedPlatform))
          }
        case None =>
          exampleProjectsService.examples().map { live =>
            val examples = toExamples(live)
            Ok(html.download(releases, examples, activator, selectedPlatform))
          }
      }
    }
  }

  private def latestActivator: Future[ActivatorRelease] = {
    implicit val timeout: Timeout = 2.seconds
    (activatorReleaseActor ? ActivatorReleaseActor.GetVersion).mapTo[ActivatorRelease]
  }

  def changelog = markdownAction("public/markdown/changelog.md", { implicit request =>
    views.html.changelog(_)
  })

  def conduct = markdownAction("public/markdown/code-of-conduct.md", { implicit request => markdown =>
    views.html.markdownPage("Code of conduct", markdown)
  })

  def communityProcess = markdownAction("public/markdown/community-process.md", { implicit request => markdown =>
    views.html.markdownPage("Community process", markdown)
  })

  def contributing = markdownAction("public/markdown/contributing.md", { implicit request => markdown =>
    views.html.markdownPage("Contributing", markdown)
  })

  def markdownAction(markdownFile: String, template: RequestHeader => Html => Html) = Action { implicit request =>
    environment.resourceAsStream(markdownFile).map { is =>
      try {
        Ok(template(request)(Html(Markdown.toHtml(IOUtils.toString(is, "utf-8"), link => (link, link)))))
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

  def certification = Action { implicit request =>
    Ok(html.certification(Certification.form))
  }

  def interest = Action(parse.tolerantFormUrlEncoded) { implicit request =>
    Certification.form.bindFromRequest().fold(
      form => BadRequest(html.certification(form)),
      form => {
        certificationDao.registerInterest(form.toCertification)
        Ok(html.interest())
      }
    )
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

    val sections = byVersion.map { case (v, p) =>
      val seeds = p.filter(_.keywords.contains("seed"))
        .groupBy(byLanguage)
        .mapValues(_.sortBy(_.displayName))

      val examples = p.filterNot(_.keywords.contains("seed"))
        .groupBy(byLanguage)
        .mapValues(_.sortBy(_.displayName))

      v -> PlayExampleSection(seeds, examples)
    }
    PlayExamples(sections)
  }
}

case class PlayExamples(sections: Seq[(String, PlayExampleSection)])

case class PlayExampleSection(seeds: Map[String, Seq[ExampleProject]],
                              examples: Map[String, Seq[ExampleProject]])
