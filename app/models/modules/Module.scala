package models.modules

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * A module
 */
case class Module(name: String, fullname: String, author: String, authorId: String, description: String, homePage: String)

object Module {
  implicit val modulesWrites: Writes[Seq[(Module, Seq[Release])]] = {

    implicit val releaseWrites: Writes[Release] = (
      (__ \ "isDefault").write[Boolean] ~
      (__ \ "version").write[String] ~
      (__ \ "matches").write[String]
    ).apply(r => (r.isDefault, r.version, r.frameworkMatch))

    implicit val moduleWrites: Writes[(Module, Seq[Release])] = (
      (__ \ "name").write[String] ~
      (__ \ "fullname").write[String] ~
      (__ \ "versions").write[Seq[Release]]
    ).apply(m => (m._1.name, m._1.fullname, m._2))

    (__ \ "modules").write[Seq[(Module, Seq[Release])]]
  }
}

/**
 * A release of a module
 */
case class Release(version: String, date: java.util.Date, frameworkMatch: String, isDefault: Boolean)