package controllers.documentation

import javax.inject.{ Inject, Provider, Singleton }
import play.api.i18n.Lang
import play.core.Router.Routes


import utils.routing._

/**
 * Documentation router
 */
@Singleton
class Router @Inject() () extends Routes {

  override val errorHandler = play.api.http.LazyHttpErrorHandler

  private var _prefix = "/"

  def withPrefix(prefix: String): Routes = {
    _prefix = prefix
    this
  }

  // var _prefix = ""

  def prefix = _prefix

  // def setPrefix(prefix: String) = _prefix = prefix

  def documentation = Nil

  val Language = "^/([A-Za-z]{2}(?:_[A-Za-z]{2})?)((?:/.*)?)$".r

  def routes = Function.unlift { rh =>
    // Check the prefix
    if (rh.path.startsWith(prefix)) {
      val path = rh.path.substring(prefix.length)

      // Extract the language and the remainder of the path (which should start with the version)
      val (lang, versionPath) = Language.findFirstMatchIn(path).map { mtch =>
        Some(Lang(mtch.group(1))) -> mtch.group(2)
      } getOrElse None -> path

      val Documentation = DocumentationController

      Some(versionPath).collect {
        case route"" => Documentation.index(lang)
        case route"/$version<1\.[^/]+>" => Documentation.v1Home(lang, version)
        case route"/$version/api/$path*" => Documentation.api(lang, version, path)
        // The docs used to be served from this path
        case route"/api/$version/$path*" => Documentation.apiRedirect(lang, version, path)
        case route"/$version<1\.[^/]+>/$page" => Documentation.v1Page(lang, version, page)
        case route"/$version<1\.[^/]+>/images/$image" => Documentation.v1Image(lang, version, image)
        case route"/$version<1\.[^/]+>/files/$file" => Documentation.v1File(lang, version, file)
        case route"/$version<1\.[^/]+>/cheatsheet/$category" => Documentation.v1Cheatsheet(lang, version, category)

        case route"/$version<2\.[^/]+>" => Documentation.home(lang, version)
        case route"/$version<2\.[^/]+>/" => Documentation.home(lang, version)
        case route"/$version<2\.[^/]+>/$page" => Documentation.page(lang, version, page)
        case route"/$version<2\.[^/]+>/resources/$path*" => Documentation.resource(lang, version, path)
        case route"/latest" => Documentation.latest(lang, "Home")
        case route"/latest/$page" => Documentation.latest(lang, page)
      }
    } else {
      None
    }
  }
}

object ReverseRouter {
  private def instance: ReverseRouter = play.api.Play.current.injector.instanceOf[ReverseRouter]
  def index(lang: Option[Lang]) = instance.index(lang)
  def home(lang: Option[Lang], version: String) = instance.home(lang, version)
  def page(lang: Option[Lang], version: String, page: String = "Home") = instance.page(lang, version, page)
  def api(version: String, path: String) = instance.api(version, path)
  def latest(lang: Option[Lang], page: String = "Home") = instance.latest(lang, page)
  def cheatsheet(lang: Option[Lang], version: String, category: String) = instance.cheatsheet(lang, version, category)
}

@Singleton
class ReverseRouter @Inject() (routerProvider: Provider[Router]) {
  private def router: Router = routerProvider.get
  def index(lang: Option[Lang]) = {
    router.prefix + lang.fold("")(l => "/" + l.code)
  }
  def home(lang: Option[Lang], version: String) = {
    s"${index(lang)}/$version"
  }
  def page(lang: Option[Lang], version: String, page: String = "Home") = {
    s"${index(lang)}/$version/$page"
  }
  def api(version: String, path: String) = {
    s"${router.prefix}/$version/api/$path"
  }
  def latest(lang: Option[Lang], page: String = "Home") = {
    this.page(lang, "latest", page)
  }
  def cheatsheet(lang: Option[Lang], version: String, category: String) = {
    s"${index(lang)}/$version/cheatsheet/$category"
  }
}