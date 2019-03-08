package controllers

import javax.inject.Inject

import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents

/**
 * The outreachy controller
 */
class Outreachy @Inject()(components: ControllerComponents)(implicit reverseRouter: documentation.ReverseRouter)
    extends AbstractController(components) {

  def outreachy = Action(Redirect(routes.Outreachy.round15()))

  def round10 = Action { implicit req =>
    Ok(views.html.outreachy.round10())
  }

  def round15 = Action { implicit req =>
    Ok(views.html.outreachy.round15())
  }
}
