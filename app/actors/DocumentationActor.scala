package actors

import actors.SitemapGeneratingActor.GenerateSitemap
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.DispatcherSelector
import akka.actor.typed.PostStop
import akka.actor.typed.PreRestart
import akka.actor.typed.Signal
import akka.actor.typed.SupervisorStrategy
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.Routers
import akka.actor.typed.scaladsl.adapter._
import akka.pattern.pipe
import akka.stream.scaladsl.Source
import akka.util.ByteString
import akka.util.Timeout
import com.google.inject.Provides
import models.documentation._
import play.api.i18n.Lang
import play.api.libs.concurrent.ActorModule
import utils.PlayGitRepository
import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * Protocol for the documentation actor.
 */
object DocumentationActor extends ActorModule {
  type Message = Command

  sealed trait Command

  /**
   * A response
   */
  sealed trait Response[+T]

  /**
   * A request
   */
  sealed trait Request[R <: Response[R]] extends Command {
    def version: Version
    def cacheId: Option[String]
    def replyTo: ActorRef[Response[R]]
  }

  /**
   * A request that has a language
   */
  sealed trait LangRequest[R <: Response[R]] extends Request[R] {
    def lang: Option[Lang]
  }

  /**
   * The resource requested was found
   */
  sealed trait Found[T] extends Response[T] {
    def cacheId: String
  }

  /**
   * The resource requested was not found.
   *
   * @param translationContext The translation context.
   */
  case class NotFound(translationContext: TranslationContext) extends Response[Nothing]

  /**
   * The resource requested was not modified
   */
  case class NotModified(cacheId: String) extends Response[Nothing]

  /**
   * A request to render a page.
   *
   * @param lang The language to render the page for, none for the default language.
   * @param version The version of the documentation to fetch the page for.
   * @param page The page to render.
   */
  case class RenderPage(
      lang: Option[Lang],
      version: Version,
      cacheId: Option[String],
      page: String,
      replyTo: ActorRef[Response[RenderedPage]],
  ) extends LangRequest[RenderedPage]

  /**
   * A rendered page.
   *
   * @param pageHtml The HTML of the page.
   * @param sidebarHtml The HTML of the sidebar, if it exists.
   * @param breadcrumbsHtml the HTML of the breadcrumbs, if it exists.
   * @param source The GitHub source of the page.
   * @param translationContext The translation context.
   * @param cacheId The cacheid.
   */
  case class RenderedPage(
      pageHtml: String,
      sidebarHtml: Option[String],
      breadcrumbsHtml: Option[String],
      source: Option[String],
      translationContext: TranslationContext,
      cacheId: String,
  ) extends Found[RenderedPage]

  /**
   * Load a resource.
   *
   * @param lang The language to load the resource for, none for the default language.
   * @param version The version of the documentation to get the resource from.
   * @param resource The resource path.
   */
  case class LoadResource(
      lang: Option[Lang],
      version: Version,
      cacheId: Option[String],
      resource: String,
      replyTo: ActorRef[Response[Resource]],
  ) extends LangRequest[Resource]

  /**
   * Load an API resource.
   *
   * @param version The version of the documentation to load the resource for.
   * @param resource The resource path.
   */
  case class LoadApi(
      version: Version,
      cacheId: Option[String],
      resource: String,
      replyTo: ActorRef[Response[Resource]],
  ) extends Request[Resource]

  /**
   * A resource.
   *
   * @param content The content of the resource.
   * @param size The size of the resource.
   */
  case class Resource(content: Source[ByteString, _], size: Long, cacheId: String) extends Found[Resource]

  /**
   * Update the documentation.
   *
   * @param documentation The documnetation to update.
   */
  case class UpdateDocumentation(documentation: Documentation) extends Command

  /**
   * Get a summary of the documentation.
   */
  case class GetSummary(replyTo: ActorRef[DocumentationSummary]) extends Command

  /**
   * Check whether the given page exists at the given version.
   */
  case class QueryPageExists(
      lang: Option[Lang],
      version: Version,
      cacheId: Option[String],
      page: String,
      replyTo: ActorRef[Response[PageExists]],
  ) extends LangRequest[PageExists]

  /**
   * The queried page exists.
   */
  case class PageExists(exists: Boolean, cacheId: String) extends Found[PageExists]

  /**
   * Render a V1 page
   *
   * @param lang The language to render the page for, none for the default language.
   * @param version The version of the documentation to fetch the page for.
   * @param page The page to render.
   */
  case class RenderV1Page(
      lang: Option[Lang],
      version: Version,
      cacheId: Option[String],
      page: String,
      replyTo: ActorRef[Response[RenderedPage]],
  ) extends LangRequest[RenderedPage]

  case class RenderV1Cheatsheet(
      lang: Option[Lang],
      version: Version,
      cacheId: Option[String],
      category: String,
      replyTo: ActorRef[Response[V1Cheatsheet]],
  ) extends LangRequest[V1Cheatsheet]

  case class V1Cheatsheet(
      sheets: Seq[String],
      title: String,
      otherCategories: Map[String, String],
      context: TranslationContext,
      cacheId: String,
  ) extends Found[V1Cheatsheet]

  /**
   * Check whether the given page exists at the given version.
   */
  case class QueryV1PageExists(
      lang: Option[Lang],
      version: Version,
      cacheId: Option[String],
      page: String,
      replyTo: ActorRef[Response[PageExists]],
  ) extends LangRequest[PageExists]

  /**
   * A summary of the documentation.
   *
   * @param defaultLatest The latest version of the default language.
   * @param defaultLang The default language.
   * @param allLangs All languages.
   * @param translations All translations and their latest versions.
   * @param translationContext The translation context.
   */
  case class DocumentationSummary(
      defaultLatest: Option[Version],
      defaultLang: Lang,
      allLangs: Seq[Lang],
      translations: Map[Lang, Option[Version]],
      translationContext: TranslationContext,
  )

  /**
   * Get a sitemap describing the documentation.
   */
  case class GetSitemap(replyTo: ActorRef[DocumentationSitemap]) extends Command

  /**
   * A sitemap describing all the pages in the documentation.
   */
  case class DocumentationSitemap(sitemap: Sitemap)

  // Not part of protocol:

  case class DocumentationGitRepos(default: DocumentationGitRepo, translations: Seq[DocumentationGitRepo])

  case class DocumentationGitRepo(config: TranslationConfig, repo: PlayGitRepository)

  @Provides
  def apply(
      config: DocumentationConfig,
      pollerFactory: DocumentationPollingActor.Factory,
  ): Behavior[Command] =
    Behaviors.setup(context => new DocumentationActor(config, pollerFactory, context).noDocumentation)
}

/**
 * The documentation actor.
 *
 * This is the entry into loading and rendering the documentation.  This actor does not block, it does not do any IO,
 * it merely coordinates everything.  It keeps a reference to the current documentation index.  All IO, page rendering,
 * etc is delegated to the other documentation actors.
 */
class DocumentationActor(
    config: DocumentationConfig,
    pollerFactory: DocumentationPollingActor.Factory,
    context: ActorContext[DocumentationActor.Command],
) {

  import DocumentationActor._
  import actors.{ DocumentationLoadingActor => Loader }

  implicit val timeout: Timeout             = Timeout(5.seconds)
  implicit val system: ActorSystem[Nothing] = context.system
  import system.executionContext

  private def createRepo(config: TranslationConfig) = {
    new DocumentationGitRepo(config, new PlayGitRepository(config.repo, config.remote, config.basePath))
  }

  private val repos = DocumentationGitRepos(
    createRepo(config.default),
    config.translations.map(createRepo),
  )

  /**
   * The poller, when it starts, scans all repos and will send the documentation,
   * moving this actor into the documentation loaded state.
   */
  private val poller = context.spawn(
    pollerFactory(repos, context.self),
    "documentationPoller",
    DispatcherSelector.fromConfig("polling-dispatcher"),
  )

  private val loader = context.spawn(
    Routers.pool(4)(Behaviors.supervise(Loader()).onFailure[Exception](SupervisorStrategy.restart)),
    "documentationLoaders",
    DispatcherSelector.fromConfig("loader-dispatcher"),
  )

  private val sitemapGenerator = context.spawn(
    SitemapGeneratingActor(),
    "sitemapGenerator",
    DispatcherSelector.fromConfig("sitemapgenerator-dispatcher"),
  )

  private def postStop: PartialFunction[(ActorContext[Command], Signal), Behavior[Command]] = {
    case (_, signal) if signal == PreRestart || signal == PostStop =>
      repos.default.repo.close()
      repos.translations.foreach(_.repo.close())
      Behaviors.same
  }

  val version21 = Version("2.1", 2, 1, 0, 0, Release)

  /**
   * Initial state, documentation not loaded, all messages buffered.
   */
  def noDocumentation: Behavior[Command] =
    Behaviors.withStash(Int.MaxValue) { buffer =>
      Behaviors
        .receiveMessage[Command] {
          case UpdateDocumentation(docs) =>
            buffer.unstashAll(documentationLoaded(docs))
          case other =>
            buffer.stash(other)
            Behaviors.same
        }
        .receiveSignal(postStop)
    }

  /**
   * Final state, documentation loaded
   */
  def documentationLoaded(documentation: Documentation): Behavior[Command] = {

    /** Do the given block for the given lang */
    def forLang[R <: Response[R]](req: LangRequest[R], replyTo: ActorRef[Response[Nothing]])(
        block: (Lang, Translation, TranslationVersion) => Future[Response[R]],
    ) = {
      val (lang, maybeTranslation) = req.lang match {
        case Some(lang) => (lang, documentation.translations.get(lang))
        case None       => (documentation.defaultLang, Some(documentation.default))
      }
      maybeTranslation match {
        case Some(translation) =>
          translation.byVersion.get(req.version) match {
            case Some(tv) =>
              if (req.cacheId.exists(_ == tv.cacheId)) {
                replyTo ! NotModified(tv.cacheId)
              } else {
                block(lang, translation, tv).pipeTo(replyTo.toClassic)
              }
            case None => replyTo ! NotFound(notFoundTranslationContext(lang, translation.displayVersions))
          }
        case None => replyTo ! NotFound(notFoundTranslationContext())
      }
    }

    def loaderRequestOpt[LR, R <: Response[R]](incomingRequest: LangRequest[R], replyTo: ActorRef[Response[R]])(
        loaderRequest: (TranslationVersion, ActorRef[Option[LR]]) => Loader.Command,
    )(response: (Lang, Translation, TranslationVersion, LR) => Response[R]) = {
      forLang(incomingRequest, replyTo) { (lang, translation, tv) =>
        loader.ask[Option[LR]](replyTo => loaderRequest(tv, replyTo)).map {
          case Some(answer) => response(lang, translation, tv, answer)
          case None         => NotFound(notFoundTranslationContext(lang, translation.displayVersions))
        }
      }
    }

    /**
     * Make a loader request for the given language and version.
     *
     * The generated message, when asked from the loader, is expected to return the given return type.
     *
     * @param incomingRequest The request message
     * @param loaderRequest A function that creates the request message from the given repository
     * @param response A function that creates the response message from the answer, to send back to the sender
     * @tparam LR the type of the response message from the loader
     * @tparam R the type of the response message from this actor
     */
    def loaderRequest[LR, R <: Response[R]](incomingRequest: LangRequest[R], replyTo: ActorRef[Response[R]])(
        loaderRequest: (TranslationVersion, ActorRef[LR]) => Loader.Command,
    )(response: (Lang, Translation, TranslationVersion, LR) => Response[R]) = {
      forLang(incomingRequest, replyTo) { (lang, translation, tv) =>
        loader.ask[LR](replyTo => loaderRequest(tv, replyTo)).map { answer =>
          response(lang, translation, tv, answer)
        }
      }
    }

    /**
     * Get the translation context for the given language and version.
     */
    def translationContext(lang: Lang, version: Version, translation: Translation): TranslationContext = {
      val isDefault = lang == documentation.defaultLang
      val defaultAlternative = AlternateTranslation(
        documentation.defaultLang,
        true,
        findMostSuitableMatch(version, documentation.default.availableVersions.map(_.version)),
      )
      val alternatives = documentation.translations.toList.sortBy(_._1.code).map { case (l, t) =>
        AlternateTranslation(l, false, findMostSuitableMatch(version, t.availableVersions.map(_.version)))
      }
      TranslationContext(
        lang,
        isDefault,
        Some(version),
        translation.displayVersions,
        defaultAlternative :: alternatives,
      )
    }

    /**
     * Find the most suitable match for the given version from the list of candidates.
     *
     * The most suitable match is defined as a version that is of the same major version, and is either an identical
     * version, or if no identical version is found, is the most recent stable version of the candidates, otherwise
     * the most recent version of the candidates.  If there are no candidates of the same major version, none is returned.
     */
    def findMostSuitableMatch(version: Version, candidates: List[Version]): Option[Version] = {
      val sameMajors = candidates.filter(_.sameMajor(version))
      sameMajors
        .find(_ == version)
        .orElse {
          sameMajors.find(v => v.versionType.isLatest || v.versionType.isStable)
        }
        .orElse(sameMajors.headOption)
    }

    /**
     * The translation context for if a resource is not found.
     */
    def notFoundTranslationContext(
        lang: Lang = documentation.defaultLang,
        displayVersions: List[Version] = documentation.default.displayVersions,
    ) = {
      TranslationContext(
        lang,
        lang == documentation.defaultLang,
        None,
        displayVersions,
        AlternateTranslation(documentation.defaultLang, true, None) ::
          documentation.translations.toList.sortBy(_._1.code).map { case (l, translation) =>
            AlternateTranslation(l, false, None)
          },
      )
    }

    Behaviors
      .receiveMessage[Command] {
        case UpdateDocumentation(docs) =>
          documentationLoaded(docs)

        case rp @ RenderPage(_, version, _, page, replyTo) =>
          loaderRequestOpt[play.doc.RenderedPage, RenderedPage](rp, replyTo) { (tv, replyTo) =>
            Loader.RenderPage(page, tv.playDoc, replyTo)
          } { (lang, translation, tv, page) =>
            val source = if (version.versionType.isLatest) {
              translation.source.map { gitHubSource =>
                gitHubSource.format(tv.symbolicName, page.path)
              }
            } else None

            RenderedPage(
              pageHtml = page.html,
              sidebarHtml = page.sidebarHtml,
              breadcrumbsHtml = page.breadcrumbsHtml,
              source = source,
              translationContext = translationContext(lang, version, translation),
              cacheId = tv.cacheId,
            )
          }
          Behaviors.same

        case rp @ RenderV1Page(_, version, _, page, replyTo) =>
          loaderRequestOpt[String, RenderedPage](rp, replyTo) { (tv, replyTo) =>
            Loader.RenderV1Page(page, tv.repo, replyTo)
          } { (lang, translation, tv, content) =>
            RenderedPage(
              pageHtml = content,
              sidebarHtml = None,
              breadcrumbsHtml = None,
              source = None,
              translationContext = translationContext(lang, version, translation),
              cacheId = tv.cacheId,
            )
          }
          Behaviors.same

        case lr: LoadResource =>
          loaderRequestOpt[Loader.Resource, Resource](lr, lr.replyTo) { (tv, replyTo) =>
            Loader.LoadResource(lr.resource, tv.repo, replyTo)
          } { (_, _, tv, resource) =>
            Resource(resource.content, resource.size, tv.cacheId)
          }
          Behaviors.same

        case LoadApi(version, cacheId, resource, replyTo) =>
          documentation.default.byVersion.get(version) match {
            case Some(tr) =>
              if (cacheId.exists(_ == tr.cacheId)) {
                replyTo ! NotModified(tr.cacheId)
              } else {
                loader
                  .ask[Option[Loader.Resource]](replyTo =>
                    Loader.LoadResource(s"api/$resource", tr.repo, replyTo),
                  )
                  .map {
                    case Some(resource) => Resource(resource.content, resource.size, tr.cacheId)
                    case None           => NotFound(notFoundTranslationContext())
                  }
                  .pipeTo(replyTo.toClassic)
              }
            case None => replyTo ! NotFound(notFoundTranslationContext())
          }
          Behaviors.same

        case GetSummary(replyTo) =>
          replyTo ! DocumentationSummary(
            documentation.default.defaultVersion,
            documentation.defaultLang,
            documentation.allLangs,
            documentation.translations.view.mapValues(_.defaultVersion).toMap,
            notFoundTranslationContext(),
          )
          Behaviors.same

        case pe @ QueryPageExists(_, _, _, page, replyTo) =>
          loaderRequest[Boolean, PageExists](pe, replyTo) { (tv, replyTo) =>
            Loader.PageExists(page, tv.playDoc, tv.repo, replyTo)
          } { (_, _, tv, exists) =>
            PageExists(exists, tv.cacheId)
          }
          Behaviors.same

        case pe @ QueryV1PageExists(_, _, _, page, replyTo) =>
          loaderRequest[Boolean, PageExists](pe, replyTo) { (tv, replyTo) =>
            Loader.V1PageExists(page, tv.repo, replyTo)
          } { (_, _, tv, exists) =>
            PageExists(exists, tv.cacheId)
          }
          Behaviors.same

        case rc @ RenderV1Cheatsheet(_, version, _, category, replyTo) =>
          loaderRequestOpt[Loader.V1Cheatsheet, V1Cheatsheet](rc, replyTo) { (tv, replyTo) =>
            Loader.RenderV1Cheatsheet(category, tv.repo, replyTo)
          } { (lang, translation, tv, cs) =>
            V1Cheatsheet(
              cs.sheets,
              cs.title,
              cs.otherCategories,
              translationContext(lang, version, translation),
              tv.cacheId,
            )
          }
          Behaviors.same

        case GetSitemap(replyTo) =>
          sitemapGenerator
            .ask[Sitemap](replyTo => GenerateSitemap(documentation, replyTo))
            .map(DocumentationSitemap)
            .pipeTo(replyTo.toClassic)
          Behaviors.same
      }
      .receiveSignal(postStop)
  }

}
