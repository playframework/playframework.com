package controllers

import org.webjars.WebJarAssetLocator
import play.api.mvc.Controller
import play.api.Play
import play.api.Play.current

object StaticWebJarAssets extends Controller {

  def at(file: String) = Assets.at("/" + WebJarAssetLocator.WEBJARS_PATH_PREFIX, file)

  lazy val maybeContentUrl = Play.configuration.getString("content-url")

  lazy val webJarAssetLocator = new WebJarAssetLocator()

  def getUrl(file: String) = {
    val path = webJarAssetLocator.getFullPath(file).stripPrefix(WebJarAssetLocator.WEBJARS_PATH_PREFIX + "/")
    val atUrl = routes.StaticWebJarAssets.at(path).url
    maybeContentUrl.fold(atUrl)(_ + atUrl)
  }

}
