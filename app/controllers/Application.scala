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
import play.api.libs.json.Json
import models._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

@Singleton
class Application @Inject() (
  environment: Environment,
  configuration: Configuration,
  val messagesApi: MessagesApi,
  certificationDao: CertificationDao,
  @Named("activator-release-actor") activatorReleaseActor: ActorRef
)(implicit ec: ExecutionContext) extends Controller with Common with I18nSupport {

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
      MovedPermanently(request.path.take(request.path.length - 1))
    } else {
      notFound
    }
  }

  def setPreferedLanguage(lang: String) = Action {
    messagesApi.setLang(Ok, Lang(lang))
  }

}
