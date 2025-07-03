package controllers

import play.api.mvc._
import play.api.mvc.Results._

trait Common {
  def notFound(using request: RequestHeader) = NotFound(views.html.notfound())

  given reverseRouter: documentation.ReverseRouter
}
