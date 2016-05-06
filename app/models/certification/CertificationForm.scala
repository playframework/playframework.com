package models.certification

import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._

case class CertificationForm(name: String, email: String, developer: Boolean, organization: Boolean, comments: String) {
  def toCertification = Certification(DateTime.now(), name, email, developer, organization, comments)
}

object CertificationForm {
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