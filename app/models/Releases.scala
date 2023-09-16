package models

import play.api.libs.json.Json
import play.api.libs.json.Reads

case class PlayRelease(
    version: String,
    private val url: Option[String],
    date: Option[String],
    size: Option[String],
    name: Option[String],
) {
  lazy val secureUrl: Option[String] = url.map(SecurifyUrl.securify)
}

object PlayRelease {
  implicit val releaseReads: Reads[PlayRelease] = Json.reads[PlayRelease]
}

case class PlayReleases(latest: PlayRelease, development: Seq[PlayRelease], previous: Seq[PlayRelease])

object PlayReleases {
  implicit val playReleasesReads: Reads[PlayReleases] = Json.reads[PlayReleases]
}

private[models] object SecurifyUrl {
  def securify(url: String): String = {
    if (url.startsWith("https")) {
      url
    } else {
      url.replaceFirst("http", "https")
    }
  }
}
