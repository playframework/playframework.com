package controllers.documentation

import actors.DocumentationActor
import actors.DocumentationActor.{ NotFound => DocsNotFound, NotModified => DocsNotModified, _ }
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import javax.inject.{Named, Inject, Singleton}
import models.documentation.{AlternateTranslation, TranslationContext, Version}
import org.joda.time.format.DateTimeFormat
import play.api.i18n.{MessagesApi, Lang}
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.duration._

import play.api.libs.MimeTypes
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.reflect.ClassTag

@Singleton
class DocumentationController @Inject() (
  messages: MessagesApi,
  @Named("documentation-actor") documentationActor: ActorRef) extends Controller {

  private implicit val timeout = Timeout(5.seconds)

  private val Rfc1123DateTimeFormat = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'").withZoneUTC()

  private val EmptyContext = TranslationContext(Lang("en"), true, None, Nil, Nil)

  private def pageNotFound(context: TranslationContext, title: String)(implicit req: RequestHeader) =
    NotFound(views.html.documentation.v2(messages, context, title))

  private def cacheable(result: Result, cacheId: String) = {
    result.withHeaders(
      ETAG -> ("\"" + cacheId + "\""),
      CACHE_CONTROL -> "max-age=10000",
      DATE -> Rfc1123DateTimeFormat.print(System.currentTimeMillis())
    )
  }

  private def notModified(cacheId: String) = cacheable(NotModified, cacheId)

  private def DocsAction(action: ActorRef => RequestHeader => Future[Result]) = Action.async(parse.empty) { implicit req =>
    action(documentationActor)(req)
  }

  private def VersionAction(version: String)(action: (ActorRef, Version) => RequestHeader => Future[Result]) = DocsAction { actor => implicit req =>
    Version.parse(version).fold(Future.successful(pageNotFound(EmptyContext, ""))) { v =>
      action(actor, v)(req)
    }
  }

  private def etag(req: RequestHeader): Option[String] = {
    req.headers.get(IF_NONE_MATCH).map { etag =>
      if (etag.startsWith("\"") && etag.endsWith("\"")) {
        etag.substring(1, etag.length - 1)
      } else etag
    }
  }

  private def preferredLang(langs: Seq[Lang])(implicit request: RequestHeader) = {
    val maybeLangFromCookie = request.cookies.get(messages.langCookieName).flatMap(c => Lang.get(c.value))
    val candidateLangs = maybeLangFromCookie match {
      case Some(cookieLang) => cookieLang +: request.acceptLanguages
      case None => request.acceptLanguages
    }
    candidateLangs.collectFirst(Function.unlift { lang =>
      langs.find(_.satisfies(lang))
    }).orElse {
      candidateLangs.collect {
        case Lang(l, c) if c.nonEmpty => Lang(l, "")
      }.collectFirst(Function.unlift { lang =>
        langs.find(_.satisfies(lang))
      })
    }
  }

  private def actorRequest[T <: DocumentationActor.Response[T]: ClassTag](actor: ActorRef, page: String,
      msg: DocumentationActor.Request[T])(block: T => Result)(implicit req: RequestHeader): Future[Result] = {
    (actor ? msg).mapTo[Response[T]].map {
      case DocsNotFound(context) => pageNotFound(context, page)
      case DocsNotModified(cacheId) => notModified(cacheId)
      case t: T => block(t)
    }
  }

  def index(lang: Option[Lang]) = latest(lang, "Home")

  //
  // Play 1 Documentation
  //

  def v1Home(lang: Option[Lang], version: String) = Action {
    Redirect(ReverseRouter.page(lang, version, "home"))
  }

  def v1Page(lang: Option[Lang], v: String, page: String) = VersionAction(v) { (actor, version) => implicit req =>
    actorRequest(actor, page, RenderV1Page(lang, version, etag(req), page)) {
      case RenderedPage(html, _, _, context, cacheId) =>
        val result = Ok(views.html.documentation.v1(messages, context, page, html))
        cacheable(withLangHeaders(result, page, context), cacheId)
    }
  }

  def v1Image(lang: Option[Lang], v: String, image: String) = {
    val resource = "images/" + image + ".png"
    ResourceAction(v, resource, (version, etag) => LoadResource(lang, version, etag, resource))
  }

  def v1File(lang: Option[Lang], v: String, file: String) =  {
    val resource = "files/" + file
    ResourceAction(v, resource, (version, etag) => LoadResource(lang, version, etag, resource), inline = false)
  }

  def v1Cheatsheet(lang: Option[Lang], v: String, category: String) = VersionAction(v) { (actor, version) => implicit req =>
    actorRequest(actor, category, RenderV1Cheatsheet(lang, version, etag(req), category)) {
      case V1Cheatsheet(sheets, title, otherCategories, context, cacheId) =>
        cacheable(
          Ok(views.html.documentation.cheatsheet(context, title, otherCategories, sheets)),
          cacheId
        )
    }
  }

  /**
   * Switch versions. Will check that the requested page exists and redirect to that if found, otherwise, redirects
   * to the home page.
   */
  def v1Switch = switchAction(QueryV1PageExists.apply, "home")

  //
  // Play 2 Documentation
  //

  def home(lang: Option[Lang], version: String) = Action {
    Redirect(ReverseRouter.page(lang, version, "Home"))
  }

  def page(lang: Option[Lang], v: String, page: String) = VersionAction(v) { (actor, version) => implicit req =>
    actorRequest(actor, page, RenderPage(lang, version, etag(req), page)) {
      case RenderedPage(html, sidebarHtml, source, context, cacheId) =>
        val result = Ok(views.html.documentation.v2(messages, context, page, Some(html), sidebarHtml, source))
        cacheable(withLangHeaders(result, page, context), cacheId)
    }
  }

  def resource(lang: Option[Lang], v: String, resource: String) =
    ResourceAction(v, resource, (version, etag) => LoadResource(lang, version, etag, resource))

  // -- API
  def v1Api(lang: Option[Lang], version: String) = Action {
    Redirect(ReverseRouter.api(version, "index.html"))
  }

  def api(lang: Option[Lang], v: String, path: String) =
    ResourceAction(v, path, (version, etag) => LoadApi(version, etag, path))

  def apiRedirect(lang: Option[Lang], version: String, path: String) =
    Action(MovedPermanently(ReverseRouter.api(version, path)))

  // -- Latest

  /**
   * When a user requests the latest for documentation, we follow the following heuristic:
   *
   * - If the user has requested a specific language, we redirect them to the latest for that specific language
   * - If the user has not specified a specific language, we use the accept header to find a preferred language,
   *   and if it matches one that we have, we redirect them to the latest for that language.
   * - Otherwise, we redirect them to the latest for the default language.
   */
  def latest(lang: Option[Lang], path: String) = DocsAction { actor => implicit req =>
    (actor ? GetSummary).mapTo[DocumentationSummary].map { summary =>
      val (selectedLang, version) = lang match {
        // requested the default lang
        case Some(l) if l == summary.defaultLang => None -> summary.defaultLatest
        // requested a specific translation
        case Some(l) if summary.translations.contains(l) => lang -> summary.translations(l)
        // requested an unknown translation
        case Some(missing) => None -> None
        case None =>
          // This is the only place where we do accept header based language detection, on the documentation home page
          val autoLang = preferredLang(summary.allLangs)
          autoLang match {
            case Some(l) if summary.translations.contains(l) => autoLang -> summary.translations(l)
            case _ => None -> summary.defaultLatest
          }
      }
      version.map { v =>
        val url = ReverseRouter.home(selectedLang, v.name)
        Redirect(s"$url/$path").withHeaders(VARY -> ACCEPT_LANGUAGE)
      }.getOrElse(pageNotFound(summary.translationContext, path))
    }
  }

  /**
   * Switch versions. Will check that the requested page exists and redirect to that if found, otherwise, redirects
   * to the home page.
   */
  def switch = switchAction(QueryPageExists.apply, "Home")

  private def ResourceAction(version: String, resource: String, message: (Version, Option[String]) => Any, inline: Boolean = true) = {
    VersionAction(version) { (actor, version) => implicit req =>
      (actor ? message(version, etag(req))).mapTo[Response[Resource]].map {
        case DocsNotFound(context) => pageNotFound(context, resource)
        case DocsNotModified(cacheId) => notModified(cacheId)
        case Resource(enumerator, size, cacheId) =>
          val fileName = resource.drop(resource.lastIndexOf('/') + 1)
          val contentDisposition = if (inline) {
            Map.empty
          } else {
            Map(CONTENT_DISPOSITION -> s"""attachment; filename="$fileName"""")
          }
          cacheable(Result(ResponseHeader(OK, Map[String, String](
            CONTENT_LENGTH -> size.toString,
            CONTENT_TYPE -> MimeTypes.forFileName(fileName).getOrElse(BINARY)
          ) ++ contentDisposition
          ), enumerator), cacheId)
      }
    }
  }

  private def switchAction(msg: (Option[Lang], Version, Option[String], String) => LangRequest[PageExists], home: String) = {
    (lang: Option[Lang], v: String, page: String) =>
      VersionAction(v) { (actor, version) => implicit req =>
        actorRequest(actor, page, msg(lang, version, etag(req), page)) {
          case PageExists(true, cacheId) =>
            cacheable(TemporaryRedirect(ReverseRouter.page(lang, version.name, page)), cacheId)
          case PageExists(false, cacheId) =>
            cacheable(TemporaryRedirect(ReverseRouter.page(lang, version.name, home)), cacheId)
        }
      }
  }

  def withLangHeaders(result: Result, page: String, context: TranslationContext)(implicit req: RequestHeader) = {
    val linkHeader = context.alternatives.filterNot(_.lang == context.lang).collect {
      case AlternateTranslation(l, isDefault, Some(v)) =>
        val url = Call("GET", ReverseRouter.page(Some(l).filterNot(_ => isDefault), v.name, page)).absoluteURL()
        s"""<$url>; rel="alternate"; hreflang="${l.code}""""
    }.mkString(", ")
    if (linkHeader.nonEmpty) {
      result.withHeaders("Link" -> linkHeader, CONTENT_LANGUAGE -> context.lang.code)
    } else {
      result.withHeaders(CONTENT_LANGUAGE -> context.lang.code)
    }
  }
}