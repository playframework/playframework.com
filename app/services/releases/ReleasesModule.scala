package services.releases

import javax.inject.Provider
import javax.inject.Inject
import javax.inject.Singleton

import com.google.inject.AbstractModule
import models.PlayRelease
import models.PlayReleases
import org.apache.commons.io.IOUtils
import play.api.Environment
import play.api.libs.json.Json

@Singleton
class PlayReleasesProvider @Inject()(environment: Environment) extends Provider[PlayReleases] {
  private lazy val releases: PlayReleases = {
    environment
      .resourceAsStream("playReleases.json")
      .flatMap { is =>
        try {
          Json.fromJson[PlayReleases](Json.parse(IOUtils.toByteArray(is))).asOpt
        } finally {
          is.close()
        }
      }
      .getOrElse(PlayReleases(PlayRelease("unknown", None, Some("unknown"), None, None), Nil, Nil))
  }

  override def get(): PlayReleases = releases
}

class ReleasesModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[PlayReleases]).toProvider(classOf[PlayReleasesProvider])
  }
}
