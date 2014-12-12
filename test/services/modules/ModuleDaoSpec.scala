package services.modules

import java.io.File

import play.api.mvc.RequestHeader
import play.api.{Configuration, Environment}
import play.api.db.{DBApi, Database}
import play.api.db.evolutions.{DynamicEvolutions, EvolutionsComponents}
import play.api.test._
import play.core.{HandleWebCommandSupport, BuildLink, WebCommands}
import services.modules.Database.SimpleDbApi

object ModuleDaoSpec extends PlaySpecification {

  "The ModuleDao" should {

    "find a particular module and revision" in withDao { dao =>
      dao.findById("deadbolt") must beSome.which { moduleVersions =>
        val (module, versions) = moduleVersions
        module.name must_== "deadbolt"
        module.author must_== "Steve Chaloner"
        versions must haveSize(18)
        versions.head.version must_== "1.5.4"
        versions.head.isDefault must beTrue
        versions(1).isDefault must beFalse
      }
    }

    "find all modules and revisions" in withDao { dao =>
      val modules = dao.findEverything
      modules must haveSize(124)
      modules.find(_._1.name == "deadbolt") must beSome.which { moduleVersions =>
        val (module, versions) = moduleVersions
        module.author must_== "Steve Chaloner"
        versions must haveSize(18)
        versions.last.version must_== "1.5.4"
      }
    }

    "find all modules like a given key" in withDao { dao =>
      val modules = dao.findAll("auth")
      modules must haveSize(2)
      modules.exists(_.name == "linkedin") must beTrue
      modules.exists(_.name == "oauth") must beTrue
    }
  }

  def withDao[T](block: DbModuleDao => T) = Database.withDatabase(
    "com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/playunittest", Map("user" -> "root")
  ) { db =>
    Evolutions.applyEvolutions(db)
    block(new DbModuleDao(db))
  }

}

object Database {

  def withInMemory[T](block: Database => T): T = withInMemory()(block)

  def withInMemory[T](options: Map[String, String] = Map.empty)(block: Database => T): T = {
    val db = play.api.db.Database.inMemory(urlOptions = options)
    try {
      block(db)
    } finally {
      db.shutdown()
    }
  }

  def withDatabase[T](driver: String, url: String, config: Map[String, _ <: Any])(block: Database => T): T = {
    val db = play.api.db.Database(driver, url, config)
    try {
      block(db)
    } finally {
      db.shutdown()
    }
  }

  class SimpleDbApi(databasesByName: Map[String, Database]) extends DBApi {
    def databases() = databasesByName.values.toSeq

    def shutdown() = databasesByName.foreach(_._2.shutdown())

    def database(name: String) = databasesByName(name)
  }

}

object Evolutions {
  def applyEvolutions(database: Database, name: String = "default") = {
    new EvolutionsComponents {
      def dbApi = new SimpleDbApi(Map(name -> database))
      def environment = Environment.simple()
      def configuration = Configuration.empty
      def webCommands = WebCommands.Noop
      def dynamicEvolutions = new DynamicEvolutions
    }.applicationEvolutions.start()
  }
}

object WebCommands {
  val Noop = new WebCommands() {
    def addHandler(handler: HandleWebCommandSupport) = ()
    def handleWebCommand(request: RequestHeader, buildLink: BuildLink, path: File) = None
  }
}
