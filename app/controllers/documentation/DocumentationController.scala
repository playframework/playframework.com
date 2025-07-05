package controllers.documentation

import actors.DocumentationActor
import actors.DocumentationActor.{ NotFound => DocsNotFound, NotModified => DocsNotModified, _ }
import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.AskPattern._
import org.apache.pekko.actor.typed.scaladsl.adapter._
import org.apache.pekko.util.Timeout
import jakarta.inject.Inject
import jakarta.inject.Singleton
import models.PlayReleases
import models.documentation.AlternateTranslation
import models.documentation.DocumentationRedirects
import models.documentation.TranslationContext
import models.documentation.Version
import org.joda.time.format.DateTimeFormat
import play.api.http.HttpEntity
import play.api.i18n.DefaultLangs
import play.api.i18n.MessagesApi
import play.api.i18n.Lang
import play.api.mvc._
import utils.HtmlHelpers

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.reflect.ClassTag

@Singleton
class DocumentationController @Inject() (
    messages: MessagesApi,
    documentationRedirects: DocumentationRedirects,
    documentationActor: ActorRef[Command],
    releases: PlayReleases,
    components: ControllerComponents,
)(using actorSystem: org.apache.pekko.actor.ActorSystem, reverseRouter: ReverseRouter)
    extends AbstractController(components) {

  private given timeout: Timeout                  = Timeout(10.seconds)
  private given typedSystem: ActorSystem[Nothing] = actorSystem.toTyped
  import typedSystem.executionContext

  private val Rfc1123DateTimeFormat = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'").withZoneUTC()

  private val EmptyContext = TranslationContext(Lang("en"), true, None, Nil, Nil)

  private def pageNotFound(context: TranslationContext, title: String, alternateVersions: Seq[Version])(using
      req: RequestHeader,
  ) =
    NotFound(views.html.documentation.v2(messages, context, title, alternateVersions = alternateVersions))

  private def cacheable(result: Result, cacheId: String) = {
    result.withHeaders(
      ETAG          -> ("\"" + cacheId + "\""),
      CACHE_CONTROL -> "max-age=10000",
      DATE          -> Rfc1123DateTimeFormat.print(System.currentTimeMillis()),
    )
  }

  private def notModified(cacheId: String) = cacheable(NotModified, cacheId)

  private def DocsAction(action: ActorRef[Command] => (RequestHeader ?=> Future[Result])) =
    Action.async(parse.empty) { case given RequestHeader =>
      action(documentationActor)
    }

  private def VersionAction(
      version: String,
  )(action: (ActorRef[Command], Version) => (RequestHeader ?=> Future[Result])) =
    DocsAction { actor =>
      Version.parse(version).fold(Future.successful(pageNotFound(EmptyContext, "", Nil))) { v =>
        action(actor, v)
      }
    }

  private def etag(using req: RequestHeader): Option[String] = {
    req.headers.get(IF_NONE_MATCH).map { etag =>
      if (etag.startsWith("\"") && etag.endsWith("\"")) {
        etag.substring(1, etag.length - 1)
      } else etag
    }
  }

  private def preferredLang(langs: Seq[Lang])(using request: RequestHeader) = {
    val maybeLangFromCookie = request.cookies.get(messages.langCookieName).flatMap(c => Lang.get(c.value))
    val candidateLangs      = maybeLangFromCookie match {
      case Some(cookieLang) => cookieLang +: request.acceptLanguages
      case None             => request.acceptLanguages
    }
    val candidates = candidateLangs ++ candidateLangs.collect {
      case l if l.country.nonEmpty => Lang(l.language)
    }
    new DefaultLangs(langs).preferred(candidates)
  }

  private def actorRequest[T <: Response[T]: ClassTag](
      actor: ActorRef[Command],
      page: String,
      msg: ActorRef[Response[T]] => DocumentationActor.Request[T],
  )(block: T => Result)(using req: RequestHeader): Future[Result] = {
    actor.ask[Response[T]](replyTo => msg(replyTo)).flatMap {
      case DocsNotFound(context) =>
        val future = Future.sequence(context.displayVersions.map(pageExists(_, page)))
        future.map(l => pageNotFound(context, page, l.flatten))
      case DocsNotModified(cacheId) =>
        Future.successful(notModified(cacheId))
      case t: T =>
        Future.successful(block(t))
    }
  }

  def pageExists(version: Version, page: String): Future[Option[Version]] = {
    documentationActor
      .ask[Response[PageExists]](replyTo => QueryPageExists(None, version, None, page, replyTo))
      .map {
        case PageExists(true, _) =>
          Some(version)
        case other =>
          None
      }
  }

  def index(lang: Option[Lang]) = latest(lang, "Home")

  //
  // Play 1 Documentation
  //

  def v1Home(lang: Option[Lang], version: String) = Action {
    Redirect(reverseRouter.page(lang, version, "home"))
  }

  def v1Page(lang: Option[Lang], v: String, page: String) = VersionAction(v) { (actor, version) =>
    actorRequest(
      actor,
      page,
      (replyTo: ActorRef[Response[RenderedPage]]) => RenderV1Page(lang, version, etag, page, replyTo),
    ) { case RenderedPage(html, _, _, _, context, cacheId) =>
      val result = Ok(views.html.documentation.v1(messages, context, page, html))
      cacheable(withLangHeaders(result, page, context), cacheId)
    }
  }

  def v1Image(lang: Option[Lang], v: String, image: String) = {
    val resource = "images/" + image + ".png"
    ResourceAction(
      v,
      resource,
      (version, etag, replyTo) => LoadResource(lang, version, etag, resource, replyTo),
    )
  }

  def v1File(lang: Option[Lang], v: String, file: String) = {
    val resource = "files/" + file
    ResourceAction(
      v,
      resource,
      (version, etag, replyTo) => LoadResource(lang, version, etag, resource, replyTo),
      inline = false,
    )
  }

  def v1Cheatsheet(lang: Option[Lang], v: String, category: String) = VersionAction(v) { (actor, version) =>
    actorRequest(
      actor,
      category,
      (replyTo: ActorRef[Response[V1Cheatsheet]]) => RenderV1Cheatsheet(lang, version, etag, category, replyTo),
    ) { case V1Cheatsheet(sheets, title, otherCategories, context, cacheId) =>
      cacheable(
        Ok(views.html.documentation.cheatsheet(context, title, otherCategories, sheets)),
        cacheId,
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
    Redirect(reverseRouter.page(lang, version))
  }

  def page(lang: Option[Lang], v: String, page: String) = VersionAction(v) { (actor, version) =>
    val linkFuture   = canonicalLinkHeader(page, version)
    val resultFuture =
      actorRequest(
        actor,
        page,
        (replyTo: ActorRef[Response[RenderedPage]]) => RenderPage(lang, version, etag, page, replyTo),
      ) { case RenderedPage(html, sidebarHtml, breadcrumbsHtml, source, context, cacheId) =>
        val pageTitle = HtmlHelpers.friendlyTitle(page)
        val result    = Ok(
          views.html.documentation
            .v2(
              messages,
              context,
              page,
              pageTitle,
              Some(html),
              sidebarHtml,
              source,
              breadcrumbs = breadcrumbsHtml,
            ),
        )
        cacheable(withLangHeaders(result, page, context), cacheId)
      }.flatMap { result =>
        if (result.header.status == NOT_FOUND) {
          documentationRedirects.redirectFor(page) match {
            case Some(redirect) =>
              pageExists(version, redirect.to).map {
                case Some(_) => Results.MovedPermanently(reverseRouter.page(lang, v, redirect.to))
                case None    => Results.MovedPermanently(redirect.to)
              }
            case None =>
              Future.successful(result)
          }
        } else {
          Future.successful(result)
        }
      }

    for {
      link   <- linkFuture
      result <- resultFuture
    } yield result.withHeaders(link: _*)
  }

  def resource(lang: Option[Lang], v: String, resource: String) =
    ResourceAction(
      v,
      resource,
      (version, etag, replyTo) => LoadResource(lang, version, etag, resource, replyTo),
    )

  // -- API
  def v1Api(lang: Option[Lang], version: String) = Action {
    Redirect(reverseRouter.api(version, "index.html"))
  }

  def api(lang: Option[Lang], v: String, path: String) =
    ResourceAction(v, path, (version, etag, replyTo) => LoadApi(version, etag, path, replyTo))

  def apiRedirect(lang: Option[Lang], version: String, path: String) =
    Action(MovedPermanently(reverseRouter.api(version, path)))

  // -- Latest

  /**
   * When a user requests the latest for documentation, we follow the following heuristic:
   *
   * - If the user has requested a specific language, we redirect them to the latest for that specific language
   * - If the user has not specified a specific language, we use the accept header to find a preferred language,
   *   and if it matches one that we have, we redirect them to the latest for that language.
   * - Otherwise, we redirect them to the latest for the default language.
   */
  def latest(lang: Option[Lang], path: String) = DocsAction { actor =>
    (actor ? GetSummary.apply).mapTo[DocumentationSummary].map { summary =>
      val (selectedLang, version) = lang match {
        // requested the default lang
        case Some(l) if l == summary.defaultLang => None -> summary.defaultLatest
        // requested a specific translation
        case Some(l) if hasLatestDocumentation(summary, l) => lang -> summary.translations(l)
        // requested an unknown translation
        case Some(missing) => None -> None
        case None          =>
          // This is the only place where we do accept header based language detection, on the documentation home page
          val autoLang = preferredLang(summary.allLangs)
          if (hasLatestDocumentation(summary, autoLang))
            Some(autoLang) -> summary.translations(autoLang)
          else
            None -> summary.defaultLatest
      }
      version
        .map { v =>
          val url = reverseRouter.home(selectedLang, v.name)
          Redirect(s"$url/$path").withHeaders(VARY -> ACCEPT_LANGUAGE)
        }
        .getOrElse(pageNotFound(summary.translationContext, path, Nil))
    }
  }

  private def hasLatestDocumentation(summary: DocumentationSummary, lang: Lang): Boolean =
    summary.translations.contains(lang) && summary.translations(lang) == summary.defaultLatest

  def sitemap = DocsAction { actor =>
    (actor ? GetSitemap.apply).mapTo[DocumentationSitemap].map { docSitemap =>
      Ok(docSitemap.sitemap.toXml)
    }
  }

  /**
   * Switch versions. Will check that the requested page exists and redirect to that if found, otherwise, redirects
   * to the home page.
   */
  def switch = switchAction(QueryPageExists.apply, "Home")

  private def ResourceAction(
      version: String,
      resource: String,
      message: (Version, Option[String], ActorRef[Response[Resource]]) => Command,
      inline: Boolean = true,
  ) = {
    VersionAction(version) { (actor, version) =>
      actor.ask[Response[Resource]](replyTo => message(version, etag, replyTo)).map {
        case DocsNotFound(context)           => pageNotFound(context, resource, Nil)
        case DocsNotModified(cacheId)        => notModified(cacheId)
        case Resource(source, size, cacheId) =>
          val fileName           = resource.drop(resource.lastIndexOf('/') + 1)
          val contentDisposition = if (inline) {
            Nil
          } else {
            Seq(CONTENT_DISPOSITION -> s"""attachment; filename="$fileName"""")
          }
          val entity =
            HttpEntity.Streamed(source, Some(size), Some(fileMimeTypes.forFileName(fileName).getOrElse(BINARY)))
          cacheable(Ok.sendEntity(entity).withHeaders(contentDisposition: _*), cacheId)
      }
    }
  }

  private def canonicalLinkHeader(page: String, version: Version) = {
    val playRelease = version.era match {
      case 3 => releases.latest3
      case _ => releases.latest2
    }
    val Array(epoch, major, minor) = playRelease.version.split("\\.", 4)
    val latestVersion              = Version.parse(s"$epoch.$major.x").get
    documentationActor
      .ask[Response[PageExists]](replyTo => QueryPageExists(None, latestVersion, None, page, replyTo))
      .map {
        case PageExists(true, _) =>
          val canonicalUrl = s"https://www.playframework.com/documentation/$latestVersion/$page"
          val link         = s"""<$canonicalUrl>; rel="canonical""""
          Seq(LINK -> link)
        case other =>
          Seq.empty
      }
  }

  private def switchAction(
      msg: (
          Option[Lang],
          Version,
          Option[String],
          String,
          ActorRef[Response[PageExists]],
      ) => LangRequest[PageExists],
      home: String,
  ) = { (lang: Option[Lang], v: String, page: String) =>
    VersionAction(v) { (actor, version) =>
      actorRequest(
        actor,
        page,
        (replyTo: ActorRef[Response[PageExists]]) => msg(lang, version, etag, page, replyTo),
      ) {
        case PageExists(true, cacheId) =>
          cacheable(TemporaryRedirect(reverseRouter.page(lang, version.name, page)), cacheId)
        case PageExists(false, cacheId) =>
          cacheable(TemporaryRedirect(reverseRouter.page(lang, version.name, home)), cacheId)
      }
    }
  }

  def withLangHeaders(result: Result, page: String, context: TranslationContext)(using
      req: RequestHeader,
  ) = {
    val linkHeader = context.alternatives
      .filterNot(_.lang == context.lang)
      .collect { case AlternateTranslation(l, isDefault, Some(v)) =>
        val url =
          Call("GET", reverseRouter.page(Some(l).filterNot(_ => isDefault), v.name, page)).absoluteURL()
        s"""<$url>; rel="alternate"; hreflang="${l.code}""""
      }
      .mkString(", ")
    if (linkHeader.nonEmpty) {
      result.withHeaders("Link" -> linkHeader, CONTENT_LANGUAGE -> context.lang.code)
    } else {
      result.withHeaders(CONTENT_LANGUAGE -> context.lang.code)
    }
  }
}

object DocumentationController {
  val LINK = "Link"
}
