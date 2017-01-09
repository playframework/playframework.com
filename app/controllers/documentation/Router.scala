package controllers.documentation

import javax.inject.{ Inject, Provider, Singleton }
import play.api.i18n.Lang
import play.api.routing.sird._
import play.utils.UriEncoding

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

  private val Language = "^/([A-Za-z]{2}(?:_[A-Za-z]{2})?)((?:/.*)?)$".r

  /**
   * The * matcher doesn't decode paths because that would lose information (/ vs %2F), but we don't care, we want to
   * decode it anyway.
   */
  private def decodePath(path: String) = UriEncoding.decodePath(path, "UTF-8")

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
        case p"/$version<\d+\.[^/]+>/api/$path*" => docController.api(lang, version, decodePath(path))
        // The docs used to be served from this path
        case p"/api/$version/$path*" => docController.apiRedirect(lang, version, path)
        // Play1 doc specific paths
        case p"/$version<1\.[^/]+>/$page" => docController.v1Page(lang, version, page)
        case p"/$version<1\.[^/]+>/images/$image" => docController.v1Image(lang, version, image)
        case p"/$version<1\.[^/]+>/files/$file" => docController.v1File(lang, version, file)
        case p"/$version<1\.[^/]+>/releases/$file" => docController.v1Page(lang, version, "releases/" + file)
        case p"/$version<1\.[^/]+>/releases/$release/$file" => docController.v1Page(lang, version, "releases/" + release + "/" + file)
        case p"/$version<1\.[^/]+>/cheatsheet/$category" => docController.v1Cheatsheet(lang, version, category)
        // Other paths
        case p"/$version<\d+\.[^/]+>" => docController.home(lang, version)
        case p"/$version<\d+\.[^/]+>/" => docController.home(lang, version)
        case p"/$version<\d+\.[^/]+>/$page" => docController.page(lang, version, page)
        case p"/$version<\d+\.[^/]+>/resources/$path*" => docController.resource(lang, version, decodePath(path))
        case p"/latest" => docController.latest(lang, "Home")
        case p"/latest/$path*" => docController.latest(lang, path)
        case p"/switch/$version<1\.[^/]+>/$page" => docController.v1Switch(lang, version, page)
        case p"/switch/$version<\d+\.[^/]+>/$page" => docController.switch(lang, version, page)
      }
    } else {
      None
    }
  }
}

@deprecated("Inject an instance of ReverseRouter instead", "2.6.0")
object ReverseRouter extends ReverseRouter(new Provider[Router] {
  def get = play.api.Play.current.injector.instanceOf[Router]
})

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
  def switch(lang: Option[Lang], version: String, page: String) = {
    s"${index(lang)}/switch/$version/$page"
  }
}
