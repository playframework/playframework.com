package controllers

import play.api.mvc._

/**
 * The outreachy controller
 */
class Outreachy extends Controller {

  def outreachy = Action(Redirect(routes.Outreachy.round11()))

  def round10 = Action { implicit req =>
    Ok(views.html.outreachy.round10())
  }

  def round11 = Action { implicit req =>
    Ok(views.html.outreachy.round11())
  }
}
