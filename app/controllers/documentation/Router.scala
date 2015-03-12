package controllers.documentation

import javax.inject.{ Inject, Provider, Singleton }
import play.api.i18n.Lang
import play.api.routing.sird._

/**
 * Documentation router
 */
@Singleton
class Router @Inject() (
  docController: DocumentationController) extends play.api.routing.Router {

  private var _prefix = "/"

  def withPrefix(prefix: String): Router = {
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

      Some(versionPath).collect {
        case p"" => docController.index(lang)
        case p"/$version<1\.[^/]+>" => docController.v1Home(lang, version)
        case p"/$version/api/$path*" => docController.api(lang, version, path)
        // The docs used to be served from this path
        case p"/api/$version/$path*" => docController.apiRedirect(lang, version, path)
        case p"/$version<1\.[^/]+>/$page" => docController.v1Page(lang, version, page)
        case p"/$version<1\.[^/]+>/images/$image" => docController.v1Image(lang, version, image)
        case p"/$version<1\.[^/]+>/files/$file" => docController.v1File(lang, version, file)
        case p"/$version<1\.[^/]+>/cheatsheet/$category" => docController.v1Cheatsheet(lang, version, category)

        case p"/$version<2\.[^/]+>" => docController.home(lang, version)
        case p"/$version<2\.[^/]+>/" => docController.home(lang, version)
        case p"/$version<2\.[^/]+>/$page" => docController.page(lang, version, page)
        case p"/$version<2\.[^/]+>/resources/$path*" => docController.resource(lang, version, path)
        case p"/latest" => docController.latest(lang, "Home")
        case p"/latest/$page" => docController.latest(lang, page)
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