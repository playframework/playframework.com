package controllers

import java.io.InputStream

import javax.inject.Inject
import javax.inject.Singleton
import models._
import org.apache.commons.io.IOUtils
import play.api._
import play.api.cache.SyncCacheApi
import play.api.i18n.I18nSupport
import play.api.i18n.Lang
import play.api.mvc._
import play.twirl.api.Html
import utils.Markdown
import views._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton
class Blog @Inject()(
    components: ControllerComponents,
                    )(implicit ec: ExecutionContext, val reverseRouter: _root_.controllers.documentation.ReverseRouter)
    extends AbstractController(components)
    with Common
    with I18nSupport {

  def index() = Action { implicit request =>
    Ok(html.blog.index("Play Framework Blog"))
  }

  def graal() = Action { implicit request =>
    Ok(html.blog.graal("Running Play on GraalVM"))
  }

  def socketio() = Action { implicit request =>
    Ok(html.blog.socketio("Play socket.io support"))
  }

}
