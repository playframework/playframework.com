package controllers

import jakarta.inject.Inject
import jakarta.inject.Singleton
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

  def index(keyword: String) = Action.async { implicit request =>
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

  def download(name: String, version: String) = Action.async { implicit request =>
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

  def documentation(name: String, version: String, page: String) = Action.async { implicit request =>
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

  def show(name: String) = Action.async { implicit request =>
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

  def dependencies(name: String, version: String) = Action.async { implicit request =>
    modulesLookup.findDependencies(name, version) match {
      case Some(yml) =>
        Future.successful(Ok(yml))
      case None =>
        Future.successful(PageNotFound)
    }
  }

  private def PageNotFound(using request: RequestHeader) = NotFound(views.html.notfound())
}
