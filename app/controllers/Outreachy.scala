package controllers

import javax.inject.Inject

import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents

import scala.concurrent.Future

/**
 * The outreachy controller
 */
class Outreachy @Inject() (components: ControllerComponents)(implicit
    reverseRouter: documentation.ReverseRouter,
) extends AbstractController(components) {

  //def outreachy = Action(Redirect(routes.Outreachy.round15))

  def round10 = Action.async { implicit req =>
    Future.successful(
      Ok(views.html.outreachy.round10())
    )
  }

  def round15 = Action.async { implicit req =>
    Future.successful(
      Ok(views.html.outreachy.round15())
    )
  }
}
