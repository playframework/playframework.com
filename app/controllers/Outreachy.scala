package controllers

import jakarta.inject.Inject

import play.api.mvc.AbstractController
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import play.api.mvc.Request

import scala.concurrent.Future

/**
 * The outreachy controller
 */
class Outreachy @Inject() (components: ControllerComponents)(using
    reverseRouter: documentation.ReverseRouter,
) extends AbstractController(components) {

  // def outreachy = Action(Redirect(routes.Outreachy.round15))

  def round10 = Action.async { case given Request[AnyContent] =>
    Future.successful(
      Ok(views.html.outreachy.round10()),
    )
  }

  def round15 = Action.async { case given Request[AnyContent] =>
    Future.successful(
      Ok(views.html.outreachy.round15()),
    )
  }
}
