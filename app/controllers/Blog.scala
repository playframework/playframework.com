package controllers

import java.io.InputStream

import jakarta.inject.Inject
import jakarta.inject.Singleton
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
class Blog @Inject() (
    components: ControllerComponents,
)(using ec: ExecutionContext, val reverseRouter: _root_.controllers.documentation.ReverseRouter)
    extends AbstractController(components)
    with Common
    with I18nSupport {

  val blogName = "Play Framework Blog"

  def index() = Action.async { case given Request[AnyContent] =>
    Future.successful(
      Ok(html.blog.index(blogName)),
    )
  }

  def graal() = Action.async { case given Request[AnyContent] =>
    Future.successful(
      Ok(html.blog.graal(blogName, "Running Play on GraalVM")),
    )
  }

  def socketio() = Action.async { case given Request[AnyContent] =>
    Future.successful(
      Ok(html.blog.socketio(blogName, "Play socket.io support")),
    )
  }

  def ossPledgeLaunch() = Action.async { case given Request[AnyContent] =>
    Future.successful(
      Ok(html.blog.ossPledgeLaunch(blogName, "Celebrating the Launch of the Open Source Pledge")),
    )
  }
}
