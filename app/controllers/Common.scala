package controllers

import play.api.mvc._

trait Common {
  self: Controller =>

  def notFound(implicit request: RequestHeader) = NotFound(views.html.notfound())
}
