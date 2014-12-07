package utils

import java.net.URI
import javax.inject.{ Inject, Singleton }
import play.api.Play
import play.api.libs.iteratee.Done
import play.api.mvc.{Results, EssentialAction, EssentialFilter}
import play.core.PlayVersion
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Adds a server header to each response
 */
object ServerHeaderFilter {

  def instance: ServerHeaderFilter = Play.current.injector.instanceOf[ServerHeaderFilter]

}

/**
 * Adds a server header to each response
 */
@Singleton
class ServerHeaderFilter @Inject() extends EssentialFilter {
  private val version = "Play/" + PlayVersion.current +
    " Scala/" + scala.util.Properties.scalaPropOrElse("version.number", "unknown")

  private val httpsPort = sys.props.get("https.port").map(_.toInt)

  private val headers = {
    val security = if (httpsPort.isDefined) {
      Seq(
        // Force HTTPS for browsers that support strict transport security, but only set a max age of 1 hour
        // while we're testing so if we stuff things up, it will only impact us for an hour.
        "Strict-Transport-Security" -> "max-age=3600"
      )
    } else Nil

    security :+ "Server" -> version
  }

  def apply(next: EssentialAction) = EssentialAction { req =>

    // If we're running on https, and the request isn't secure, upgrade
    // Exclude Play 1 module repository, as it can't handle redirects
    if (!req.secure && httpsPort.isDefined && !req.path.startsWith("/modules/")) {

      // Calculate the URI, taking into account that it could be an absolute URI
      val uri = if (req.uri.startsWith("/")) {
        req.uri
      } else {
        val u = URI.create(req.uri)
        Option(u.getRawPath).getOrElse("/") + Option(u.getRawQuery).fold("")("?" + _)
      }

      // If the request came in on the default port (80), then redirect to the default port, otherwise redirect to
      // the configured https port
      val redirect = if (req.host.contains(":")) {
        val host = req.host.split(':').head + ":" + httpsPort.get
        Results.MovedPermanently("https://" + host + uri)
      } else {
        Results.MovedPermanently("https://" + req.host + uri)
      }

      Done(redirect.withHeaders(headers: _*))
    } else {
      next(req).map { result =>
        result.withHeaders(headers: _*)
      }
    }
  }
}
