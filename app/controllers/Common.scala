package controllers

import play.api.mvc._

trait Common {
  self: BaseController =>

  def notFound(implicit request: RequestHeader) = NotFound(views.html.notfound())
}
