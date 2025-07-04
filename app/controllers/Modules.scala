package controllers

import javax.inject.Inject
import javax.inject.Singleton
import play.api.mvc._
import play.api.libs.json._
import services.modules._
import models.modules._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton
class Modules @Inject() (modulesLookup: ModulesLookup, moduleDao: ModuleDao, components: ControllerComponents)(
    using
    ec: ExecutionContext,
    reverseRouter: documentation.ReverseRouter,
) extends AbstractController(components) {

  def index(keyword: String) = Action.async { case given Request[AnyContent] =>
    Future.successful(
      render {
        case Accepts.Html() =>
          Ok(views.html.modules.list(moduleDao.findAll(keyword)))
        case Accepts.Json() =>
          import Module.modulesWrites
          Ok(Json.toJson(moduleDao.findEverything()))
      },
    )
  }

  def download(name: String, version: String) = Action.async { case given Request[AnyContent] =>
    modulesLookup.findModule(name, version) match {
      case Some(zip) =>
        Future.successful(
          Ok.sendFile(zip),
        )
      case None =>
        Future.successful(
          PageNotFound,
        )
    }
  }

  def documentation(name: String, version: String, page: String) = Action.async {
    case given Request[AnyContent] =>
      modulesLookup.loadModuleDocumentation(name, version, page) match {
        case Some(content) =>
          Future.successful(
            Ok(views.html.modules.documentation(name, content)),
          )
        case None =>
          Future.successful(
            PageNotFound,
          )
      }
  }

  def show(name: String) = Action.async { case given Request[AnyContent] =>
    moduleDao.findById(name) match {
      case Some((module, releases)) =>
        Future.successful(
          Ok(views.html.modules.show(module, releases)),
        )
      case None =>
        Future.successful(
          PageNotFound,
        )
    }
  }

  def dependencies(name: String, version: String) = Action.async { case given Request[AnyContent] =>
    modulesLookup.findDependencies(name, version) match {
      case Some(yml) =>
        Future.successful(Ok(yml))
      case None =>
        Future.successful(PageNotFound)
    }
  }

  private def PageNotFound(using request: RequestHeader) = NotFound(views.html.notfound())
}
