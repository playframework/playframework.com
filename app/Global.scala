import play.api.libs.concurrent._
import Execution.Implicits._
import play.api.{Logger, Application}
import play.filters.gzip.GzipFilter
import scala.concurrent.duration._
import play.api.mvc._
import utils.PlayGitRepository

object Global extends WithFilters(new GzipFilter(), new utils.ServerHeaderFilter)