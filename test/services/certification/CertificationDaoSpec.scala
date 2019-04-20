package services.certification

import models.certification.Certification
import org.joda.time.DateTime
import org.specs2.mutable.Specification
import play.api.db.evolutions.Evolutions
import play.api.db.Databases

object CertificationDaoSpec extends Specification {

  "The CertificationDao" should {
    sequential

    "register interest in certification" in withDao { dao =>
      val certification = Certification(
        DateTime.now().withMillis(0),
        "Grace Hopper",
        "grace@hopper.org",
        true,
        false,
        "I like certifications",
      )
      dao.registerInterest(certification)
      dao.findAllInterested() must_== Seq(certification)
    }
  }

  def withDao[T](block: DbCertificationDao => T) =
    Databases.withDatabase(
      driver = "com.mysql.jdbc.Driver",
      url = "jdbc:mysql://localhost:3306/playunittest",
      config = Map("user" -> "root"),
    ) { db =>
      Evolutions.withEvolutions(db) {
        block(new DbCertificationDao(db))
      }
    }

}
