package controllers

import java.util.Date

import models.modules.{Release, Module}
import org.specs2.mock.Mockito
import play.api.libs.json._
import play.api.test._
import services.modules.{ModuleDao, ModulesLookup}

object ModulesSpec extends PlaySpecification with Mockito {

  val module = Module("name", "Full name", "Some Author", "authorid", "Some Description", "http://www.example.com")
  val release = Release("version", new Date(), "match", true)

  "The modules controller" should {
    "render an index page" in {
      "as html when the client accepts html" in new WithApplication() {
        val (modules, _, dao) = createModules
        dao.findAll("foo") returns Seq(module)
        val result = modules.index("foo")(FakeRequest().withHeaders(ACCEPT -> "text/html"))
        status(result) must_== 200
        contentType(result) must beSome("text/html")
        contentAsString(result) must contain("Some Description")
      }

      "as json when the client accepts json" in new WithApplication() {
        val (modules, _, dao) = createModules
        dao.findEverything() returns Seq(module -> Seq(release))
        val result = modules.index("")(FakeRequest().withHeaders(ACCEPT -> "application/json"))
        status(result) must_== 200
        contentType(result) must beSome("application/json")
        val json = contentAsJson(result)
        json \ "modules" must beLike {
          case JsArray(Seq(m)) =>
            m \ "name" must_== JsString("name")
            m \ "fullname" must_== JsString("Full name")
            m \ "versions" must beLike {
              case JsArray(Seq(r)) =>
                r \ "version" must_== JsString("version")
                r \ "matches" must_== JsString("match")
                r \ "isDefault" must_== JsBoolean(true)
            }
        }
      }

    }
  }

  def createModules: (Modules, ModulesLookup, ModuleDao) = {
    val lookup = mock[ModulesLookup]
    val dao = mock[ModuleDao]
    val modules = new Modules(lookup, dao)
    (modules, lookup, dao)
  }

}
