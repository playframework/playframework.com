package services.github

import org.slf4j.LoggerFactory
import play.api.{Configuration, Environment}
import play.api.inject.Module

class GitHubModule extends Module {
  private val log = LoggerFactory.getLogger(classOf[GitHubModule])

  def bindings(environment: Environment, configuration: Configuration) = {
    import scala.collection.JavaConverters._
    val committerTeams = configuration.underlying.getStringList("github.committerTeams").asScala
    val organisation = configuration.underlying.getString("github.organisation")
    val gitHubApiUrl = configuration.underlying.getString("github.apiUrl")

    configuration.getOptional[String]("github.access.token") match {
      case Some(accessToken) =>
        Seq(
          bind[GitHubConfig].to(GitHubConfig(accessToken, gitHubApiUrl, organisation, committerTeams)),
          bind[GitHub].to[DefaultGitHub],
          bind[ContributorsSummariser].qualifiedWith("gitHubContributorsSummariser").to[DefaultContributorsSummariser],
          bind[ContributorsSummariser].to[CachingContributorsSummariser]
        )
      case None =>
        log.info("No GitHub access token yet, using fallback contributors")
        Seq(bind[ContributorsSummariser].to[OfflineContributorsSummariser])
    }

  }
}
