package utils

import javax.inject.{ Inject, Singleton }
import play.filters.gzip.GzipFilter
import play.api.http.HttpFilters
import play.api.mvc._

@Singleton
class Filters @Inject() (gzipFilter: GzipFilter, serverHeaderFilter: ServerHeaderFilter) extends HttpFilters {
  override val filters = Seq(gzipFilter, serverHeaderFilter)
}