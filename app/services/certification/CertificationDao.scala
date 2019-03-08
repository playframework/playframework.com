package services.certification

import javax.inject.Inject
import javax.inject.Singleton

import anorm.SqlParser._
import anorm._
import com.google.inject.ImplementedBy
import models.certification.Certification
import org.joda.time.DateTime
import play.api.db.Database

/**
 * DAO for accessing certifications
 */
@ImplementedBy(classOf[DbCertificationDao])
trait CertificationDao {

  /**
   * Someone has registered interest in certification
   */
  def registerInterest(certification: Certification): Unit

  /**
   * Find all people that have registered interest in certification
   */
  def findAllInterested(): Seq[Certification]
}

@Singleton
class DbCertificationDao @Inject()(db: Database) extends CertificationDao {

  import JodaParameterMetaData._

  private val certificationParser = {
    (get[DateTime]("Certification.registered") ~
      get[String]("Certification.name") ~
      get[String]("Certification.email") ~
      get[Boolean]("Certification.developer") ~
      get[Boolean]("Certification.organization") ~
      get[String]("Certification.comments")).map(flatten).map((Certification.apply _).tupled)
  }

  def registerInterest(certification: Certification) = {
    db.withConnection { implicit conn =>
      SQL"""insert into Certification (registered, name, email, developer, organization, comments)
          value (${certification.registered}, ${certification.name}, ${certification.email},
          ${certification.developer}, ${certification.organization}, ${certification.comments})
       """.execute()
    }
  }

  def findAllInterested() = {
    db.withConnection { implicit conn =>
      SQL"select * from Certification".as(certificationParser.*)
    }
  }
}
