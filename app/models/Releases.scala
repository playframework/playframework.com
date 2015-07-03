package models

import play.api.libs.json.Json

case class PlayRelease(version: String, private val url: Option[String], date: Option[String], size: Option[String], name: Option[String]) {
  lazy val secureUrl: Option[String] = url.map(SecurifyUrl.securify)
}

object PlayRelease {
  implicit val releaseReads = Json.reads[PlayRelease]
}

case class PlayReleases(latest: PlayRelease, development: Seq[PlayRelease], previous: Seq[PlayRelease])

object PlayReleases {
  implicit val playReleasesReads = Json.reads[PlayReleases]
}

case class ActivatorRelease(version: String, private val url: String, private val miniUrl: String, size: String, miniSize: String,
  playVersion: String, akkaVersion: String, scalaVersion: String) {

  lazy val secureUrl: String = SecurifyUrl.securify(url)
  lazy val miniSecureUrl: String = SecurifyUrl.securify(miniUrl)
}

object ActivatorRelease {
  implicit val activatorReads = Json.reads[ActivatorRelease]
}

private[models] object SecurifyUrl {
  def securify(url: String): String = url.replaceFirst("http", "https")
}
