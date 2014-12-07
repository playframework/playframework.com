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

/** Uncomment the following lines as needed **/
/**
import play.api.libs._
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import java.util.concurrent._
import scala.concurrent.stm._
import akka.util.duration._
import play.api.cache._
**/

// trait Commons {
//   self: Results =>
  
//   // implicit def latestDownloads: LatestDownloads = {
//   //   val (official, upcoming) = Download.versions
//   //   LatestDownloads(
//   //     official.filter(_._1.startsWith("2")).headOption.map(_._1),
//   //     upcoming.headOption.map(_._1)
//   //   )
//   // }
  
//   def PageNotFound = NotFound(views.html.defaultpages.notFound())
  
//   def Error(t: Throwable) = InternalServerError(views.html.defaultpages.error(t))
  
//   def Developers[A](a: String => Action[A]) = Security.Authenticated(
//     _.session.get("developer"),
//     _ => Forbidden(views.html.notFound())
//   )(a)
  
// }

//case class LatestDownloads(latest: Option[String], latestRC: Option[String])

object Modules {

  private def instance: Modules = Play.current.injector.instanceOf[Modules]

  def index(keyword: String) = instance.index(keyword)

  def download(name: String, version: String) = instance.download(name, version)

  def documentation(name: String, version: String, page: String) = instance.documentation(name, version, page)

  def show(name: String) = instance.show(name)

  def dependencies(name: String, version: String) = instance.dependencies(name, version)

}

@Singleton
class Modules @Inject() (
  environment: Environment) extends Controller {

  val current = "hide Play.current"

  def index(keyword: String) = Action { implicit request =>
    request.headers.get(ACCEPT).filter(_ == "application/json").map { _ =>
      Ok {
        JsObject(Seq(
          "modules" -> JsArray(
            Module.findEverything.map {
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
      Ok(views.html.modules.list(Module.findAll(keyword)))
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
    Module.findById(name).map {
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

