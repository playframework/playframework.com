package controllers

import javax.inject.{ Inject, Singleton }
import play.api.mvc._
import play.api.libs.json._
import services.modules._
import models.modules._

@Singleton
class Modules @Inject() (
  modulesLookup: ModulesLookup,
  moduleDao: ModuleDao) extends Controller {

  def index(keyword: String) = Action { implicit request =>
    render {
      case Accepts.Html() =>
        Ok(views.html.modules.list(moduleDao.findAll(keyword)))
      case Accepts.Json() =>
        import Module.modulesWrites
        Ok(Json.toJson(moduleDao.findEverything()))
    }
  }

  def download(name: String, version: String) = Action { implicit request =>
    modulesLookup.findModule(name, version) match {
      case Some(zip) => Ok.sendFile(zip)
      case None => PageNotFound
    }
  }
 
    
  def documentation(name: String, version: String, page: String) = Action { implicit request =>
    modulesLookup.loadModuleDocumentation(name, version, page) match {
      case Some(content) =>
        Ok(views.html.modules.documentation(name, content))
      case None => PageNotFound
    }
  }
  
              

  def show(name: String) = Action { implicit request =>
    moduleDao.findById(name) match {
      case Some((module, releases)) => Ok(views.html.modules.show(module, releases))
      case None => PageNotFound
    }
  }

  def dependencies(name: String, version: String) = Action { implicit request =>
    modulesLookup.findDependencies(name, version) match {
      case Some(yml) => Ok(yml)
      case None => PageNotFound
    }
  }

  private def PageNotFound(implicit request: RequestHeader) = NotFound(views.html.notfound())
}

