import play.api.libs.concurrent._
import Execution.Implicits._
import play.api.{Logger, Application}
import play.filters.gzip.GzipFilter
import scala.concurrent.duration._
import play.api.mvc._
import utils.PlayGitRepository

object Global extends WithFilters(new GzipFilter(), utils.ServerHeaderFilter.instance) {
  override def onStart(app: Application) {
    implicit val a = app

    app.configuration.getString("github.access.token") match {
      case Some(accessToken) =>
        Akka.system.scheduler.schedule(0 seconds, 24 hours) {
          Logger.info("Fetching GitHub contributors...")
          controllers.Code.fetchContributors(accessToken)
        }
      case None =>
        Logger.info("Not fetching GitHub contributors because no github.access.token is set.")
    }
  }
}
