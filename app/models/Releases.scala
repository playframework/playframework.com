package models

import play.api.libs.json.Json

case class PlayRelease(version: String, url: Option[String], date: Option[String], size: Option[String], name: Option[String])

object PlayRelease {
  implicit val releaseReads = Json.reads[PlayRelease]
}

case class PlayReleases(latest: PlayRelease, development: Seq[PlayRelease], previous: Seq[PlayRelease])

object PlayReleases {
  implicit val playReleasesReads = Json.reads[PlayReleases]
}

case class ActivatorRelease(version: String, url: String, miniUrl: String, size: String, miniSize: String,
  playVersion: String, akkaVersion: String, scalaVersion: String)

object ActivatorRelease {
  implicit val activatorReads = Json.reads[ActivatorRelease]
}
