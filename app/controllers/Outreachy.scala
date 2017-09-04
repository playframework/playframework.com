package controllers

import play.api.mvc._

/**
 * The outreachy controller
 */
class Outreachy extends Controller {

  def outreachy = Action(Redirect(routes.Outreachy.round15()))

  def round10 = Action { implicit req =>
    Ok(views.html.outreachy.round10())
  }

  def round15 = Action { implicit req =>
    Ok(views.html.outreachy.round15())
  }
}
