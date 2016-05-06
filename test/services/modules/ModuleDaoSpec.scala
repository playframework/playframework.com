package services.modules

import modules.DbModuleDao
import org.specs2.mutable.Specification
import play.api.db.evolutions.Evolutions
import play.api.db.Databases

object ModuleDaoSpec extends Specification {

  "The ModuleDao" should {
    sequential

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
      val modules = dao.findEverything()
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

  def withDao[T](block: DbModuleDao => T) = Databases.withDatabase(
    driver = "com.mysql.jdbc.Driver",
    url = "jdbc:mysql://localhost:3306/playunittest",
    config = Map("user" -> "root")
  ) { db =>
    Evolutions.withEvolutions(db) {
      block(new DbModuleDao(db))
    }
  }

}
