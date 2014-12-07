package controllers

import javax.inject.{ Inject, Singleton }
import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._
import models._
import java.util.zip._
import utils._
import scala.io.Source

@Singleton
class Modules @Inject() (
  environment: Environment,
  moduleFinder: ModuleFinder) extends Controller {

  val current = "hide Play.current"

  def index(keyword: String) = Action { implicit request =>
    request.headers.get(ACCEPT).filter(_ == "application/json").map { _ =>
      Ok {
        JsObject(Seq(
          "modules" -> JsArray(
            moduleFinder.findEverything.map {
              case (module, releases) => JsObject(Seq(
                "name" -> JsString(module.name),
                "fullname" -> JsString(module.fullname),
                "versions" -> JsArray(
                  releases.map { release =>
                    JsObject(Seq(
                      "isDefault" -> JsBoolean(release.isDefault),
                      "version" -> JsString(release.version),
                      "matches" -> JsString(release.frameworkMatch)
                    ))
                  }.toList
                )
              ))
            }.toList
          )
        ))
      }
    }.getOrElse {
      Ok(views.html.modules.list(moduleFinder.findAll(keyword)))
    }
  }

  def download(name: String, version: String) = Action { implicit request =>
    environment.getExistingFile("data/modules/" + name + "-" + version + ".zip").map {
      case zip if request.method == "GET" => Ok.sendFile(zip): Result
      case zip if request.method == "HEAD" => Ok.withHeaders(CONTENT_LENGTH -> zip.length.toString): Result
    }.getOrElse(PageNotFound)
  }  

  def documentation(name: String, version: String, page: String) = Action { implicit request =>
    environment.getExistingFile("data/modules/" + name + "-" + version + ".zip").map(new ZipFile(_)).flatMap { zip =>
      try {
        Option(zip.getEntry("documentation/manual/" + page + ".textile")).map { entry =>
          Source.fromInputStream(zip.getInputStream(entry)).mkString
        }
      } finally {
        zip.close()
      }
    }.map { textile =>
      val content = Textile.toHTML(textile)
      Ok(views.html.modules.documentation(name, content))
    }.getOrElse(PageNotFound)
  }

  def show(name: String) = Action { implicit request =>
    moduleFinder.findById(name).map {
      case (module, releases) => Ok(views.html.modules.show(module, releases))
    }.getOrElse(PageNotFound)
  }

  private def PageNotFound(implicit request: RequestHeader) = NotFound(views.html.notfound())

  def dependencies(name: String, version: String) = Action { implicit request =>
    environment.getExistingFile("data/modules/" + name + "-" + version + ".zip").map(new ZipFile(_)).flatMap { zip =>
      try {
        Option(zip.getEntry("conf/dependencies.yml")).map { entry =>
          Source.fromInputStream(zip.getInputStream(entry)).mkString
        }
      } finally {
        zip.close()
      }
    }.map {
      case yml if request.method == "GET" => Ok(yml): Result
      case yml if request.method == "HEAD" => Ok: Result
    }.getOrElse(Ok("self: play -> " + name + " " + version))
  }

}

