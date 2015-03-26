package models.certification

import org.joda.time.DateTime
import play.api.data._
import play.api.data.Forms._

/**
 * Interest in a certification
 */
case class Certification(registered: DateTime, name: String, email: String, developer: Boolean, organization: Boolean, comments: String)
case class CertificationForm(name: String, email: String, developer: Boolean, organization: Boolean, comments: String) {
  def toCertification = Certification(DateTime.now(), name, email, developer, organization, comments)
}

object Certification {
  val form = Form(
    mapping(
      "name" -> text,
      "email" -> email,
      "developer" -> boolean,
      "organization" -> boolean,
      "comments" -> text
    )(CertificationForm.apply)(CertificationForm.unapply)
  )
}