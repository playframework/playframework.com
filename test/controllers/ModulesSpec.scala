package controllers

import java.util.Date

import models.modules.{Release, Module}
import org.specs2.mock.Mockito
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json._
import play.api.test._
import services.modules.{ModuleDao, ModulesLookup}

import play.api.inject.bind

import scala.concurrent.ExecutionContext.Implicits.global

object ModulesSpec extends PlaySpecification with Mockito {

  val module = Module("name", "Full name", "Some Author", "authorid", "Some Description", "http://www.example.com")
  val release = Release("version", new Date(), "match", isDefault = true)

  val moduleDao = {
    val dao = mock[ModuleDao]
    dao.findAll("foo") returns Seq(module)
    dao.findEverything() returns Seq(module -> Seq(release))
    dao
  }

  def app = new GuiceApplicationBuilder()
    .configure("play.modules.evolutions.db.default.enabled" -> false)
    .overrides(bind[ModuleDao].toInstance(moduleDao))
    .build()

  "The modules controller" should {
    "render an index page" in {
      "as html when the client accepts html" in new WithApplication(app) {
        val (modules, _, dao) = createModules(app)
        val result = modules.index("foo")(FakeRequest().withHeaders(ACCEPT -> "text/html"))
        status(result) must_== 200
        contentType(result) must beSome("text/html")
        contentAsString(result) must contain("Some Description")
      }

      "as json when the client accepts json" in new WithApplication(app) {
        val (modules, _, dao) = createModules(app)
        dao.findEverything() returns Seq(module -> Seq(release))
        val result = modules.index("")(FakeRequest().withHeaders(ACCEPT -> "application/json"))
        status(result) must_== 200
        contentType(result) must beSome("application/json")
        val json = contentAsJson(result)
        (json \ "modules").as[Seq[JsValue]] must beLike {
          case Seq(m) =>
            (m \ "name").as[String] must_== "name"
            (m \ "fullname").as[String] must_== "Full name"
            (m \ "versions").as[Seq[JsValue]] must beLike {
              case Seq(r) =>
                (r \ "version").as[String] must_== "version"
                (r \ "matches").as[String] must_== "match"
                (r \ "isDefault").as[Boolean] must beTrue
            }
        }
      }

    }
  }

  def createModules(app: play.api.Application): (Modules, ModulesLookup, ModuleDao) = {
    val lookup = app.injector.instanceOf[ModulesLookup]
    val dao = app.injector.instanceOf[ModuleDao]
    val modules = app.injector.instanceOf[Modules]
    (modules, lookup, dao)
  }
}
