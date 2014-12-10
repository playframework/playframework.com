package services

import javax.inject.Inject

import akka.actor.ActorSystem
import com.damnhandy.uri.template.UriTemplate
import com.google.inject.name.Named
import play.api.{Configuration, Environment, Logger}
import play.api.http.HeaderNames
import play.api.inject.Module
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.ws._
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future
import scala.concurrent.duration._
import models.github._

import scala.util.{Success, Failure}

/**
 * Interface to making remote calls on GitHub
 */
trait GitHub {

  /**
   * Get the organisation
   */
  def fetchOrganisation(organisation: String): Future[Organisation]

  /**
   * List all the teams for the given organisation
   */
  def fetchOrganisationTeams(organisation: Organisation): Future[Seq[Team]]

  /**
   * List all the members of the given team
   */
  def fetchTeamMembers(team: Team): Future[Seq[GitHubUser]]

  /**
   * Fetch the members of the given organisation
   */
  def fetchOrganisationMembers(organisation: Organisation): Future[Seq[GitHubUser]]

  /**
   * Fetch the details (including the name) of the given user
   */
  def fetchUserDetails(user: GitHubUser): Future[GitHubUser]

  /**
   * Fetch all the repos for the given organisation
   */
  def fetchOrganisationRepos(organisation: Organisation): Future[Seq[Repository]]

  /**
   * Fetch all the contributors for the given repository
   */
  def fetchRepoContributors(repo: Repository): Future[Seq[(GitHubUser, Int)]]
}

trait ContributorsSummariser {
  /**
   * Fetch and summarise the contributors from GitHub.
   */
  def fetchContributors: Future[Contributors]
}

case class GitHubConfig(accessToken: String, gitHubApiUrl: String, organisation: String, committerTeams: Seq[String])

class DefaultGitHub @Inject() (ws: WSClient, config: GitHubConfig) extends GitHub {

  private def authCall(url: String) = {
    ws.url(url).withHeaders(HeaderNames.AUTHORIZATION -> ("token " + config.accessToken))
  }

  private def load[T: Reads](path: String) = {
    val url = if (path.matches("https?://.*")) path else config.gitHubApiUrl + path
    authCall(url).get().map { response => 
      checkSuccess(response).json.as[T]
    }
  }

  object NextLink {
    val ParseNext = """.*<([^>]+)>;\s*rel="next".*""".r
    def unapply(response: WSResponse): Option[String] = {
      response.header("Link").collect {
        case ParseNext(next) => next
      }
    }
  }

  private def responseFailure(response: WSResponse): Exception = response.status match {
    case 403 => new RuntimeException("Request forbidden, GitHub quota rate limit is probably exceeded: " + response.body)
    case error => new RuntimeException("Request failed with " + response.status + " " +
      response.underlying[com.ning.http.client.Response].getStatusText)
  }

  private def checkSuccess(response: WSResponse): WSResponse = response.status match {
    case ok if ok < 300 => response
    case error => throw responseFailure(response)
  }

  /**
   * Loads all pages from a GitHub API endpoint that uses Link headers for paging
   */
  private def loadWithPaging[T: Reads](path: String): Future[Seq[T]] = {
    val resource = if (path.matches("https?://.*")) path else config.gitHubApiUrl + path
    val firstUrl = if (resource.contains("?")) resource + "&per_page=100" else resource + "?per_page=100"

    def loadNext(url: String): Future[Seq[T]] = {
      authCall(url).get().flatMap {
        case notOk if notOk.status >= 300 => throw responseFailure(notOk)
        case response @ NextLink(next) => for {
          nextResults <- loadNext(next)
        } yield {
          response.json.as[Seq[T]] ++ nextResults
        }
        case lastResponse => Future.successful(lastResponse.json.as[Seq[T]])
      }
    }

    loadNext(firstUrl)
  }

  private def expand(uriTemplate: String) = UriTemplate.fromTemplate(uriTemplate).expand()

  def fetchOrganisation(organisation: String) =
    load[Organisation]("/orgs/" + organisation)
  
  def fetchOrganisationTeams(organisation: Organisation) =
    loadWithPaging[Team]("/orgs/" + organisation.login + "/teams")

  def fetchTeamMembers(team: Team) = 
    loadWithPaging[GitHubUser](expand(team.membersUrl))

  def fetchOrganisationMembers(organisation: Organisation) = 
    loadWithPaging[GitHubUser](expand(organisation.membersUrl))

  def fetchUserDetails(user: GitHubUser) = 
    load[GitHubUser](expand(user.url))

  def fetchRepoContributors(repo: Repository) =
    loadWithPaging[(GitHubUser, Int)](expand(repo.contributorsUrl))(
      (implicitly[Reads[GitHubUser]] and (__ \ "contributions").read[Int]).tupled
    )

  def fetchOrganisationRepos(organisation: Organisation) = 
    loadWithPaging[Repository](expand(organisation.reposUrl))
}

class DefaultContributorsSummariser @Inject() (gitHub: GitHub, config: GitHubConfig) extends ContributorsSummariser {

  private def fetchCommitters(teams: Seq[Team]) = {
    val committerTeams = teams.filter(team => config.committerTeams.contains(team.name))
    for {
      members <- Future.sequence(committerTeams.map(gitHub.fetchTeamMembers))
      details <- Future.sequence(members.flatten.map(gitHub.fetchUserDetails))
    } yield details
  }

  private def fetchAllContributors(repos: Seq[Repository]): Future[Seq[GitHubUser]] = {
    val contributorRepos = repos.filterNot(_.fork)
    for {
      contributors <- Future.sequence(contributorRepos.map(gitHub.fetchRepoContributors))
    } yield {
      contributors
        .flatten
        .groupBy(_._1.id)
        .map {
          case ((_, contributions @ ((user, _) :: _))) =>
            user -> contributions.map(_._2).reduce(_ + _)
        }.toSeq
        .sortBy(_._2)
        .reverse
        .map(_._1)
    }
  }

  def fetchContributors = {
    for {
      organisation <- gitHub.fetchOrganisation(config.organisation)

      teams <- gitHub.fetchOrganisationTeams(organisation)
      members <- gitHub.fetchOrganisationMembers(organisation)
      repos <- gitHub.fetchOrganisationRepos(organisation)

      committers <- fetchCommitters(teams)
      contributors <- fetchAllContributors(repos)
    } yield {

      val memberIds = members.map(_.id).toSet
      val filteredMembers = members.filterNot(m => committers.exists(_.id == m.id))
      val filteredContributors = contributors.filterNot(c => memberIds.contains(c.id))

      Contributors(committers, filteredMembers, filteredContributors)
    }
  }
}

class CachingContributorsSummariser @Inject() (actorSystem: ActorSystem,
        @Named("gitHubContributorsSummariser") delegate: ContributorsSummariser) extends ContributorsSummariser {
  @volatile private var contributors: Contributors = FallbackContributors.contributors

  actorSystem.scheduler.schedule(0 seconds, 24 hours) {
    delegate.fetchContributors.onComplete {
      case Failure(t) => Logger.error("Unable to load contributors from GitHub", t)
      case Success(cs) =>
        contributors = cs
        println(FallbackContributors.dumpContributors(contributors))
        val count = contributors.committers.size + contributors.playOrganisation.size + contributors.contributors.size
        Logger.info("Loaded " + count + " contributors for GitHub")
    }
  }

  /**
   * Fetch and summarise the contributors from GitHub.
   */
  def fetchContributors = Future.successful(contributors)
}

/**
 * For use "offline", ie in development, when you don't have a GitHub access token
 */
class OfflineContributorsSummariser extends ContributorsSummariser {
  def fetchContributors = Future.successful(FallbackContributors.contributors)
}

class GitHubModule extends Module {

  def bindings(environment: Environment, configuration: Configuration) = {
    import scala.collection.JavaConverters._
    val committerTeams = configuration.underlying.getStringList("github.committerTeams").asScala
    val organisation = configuration.underlying.getString("github.organisation")
    val gitHubApiUrl = configuration.underlying.getString("github.apiUrl")

    configuration.getString("github.access.token") match {
      case Some(accessToken) =>
        Seq(
          bind[GitHubConfig].to(GitHubConfig(accessToken, gitHubApiUrl, organisation, committerTeams)),
          bind[GitHub].to[DefaultGitHub],
          bind[ContributorsSummariser].qualifiedWith("gitHubContributorsSummariser").to[DefaultContributorsSummariser],
          bind[ContributorsSummariser].to[CachingContributorsSummariser]
        )
      case None =>
        Logger.info("No GitHub access token yet, using fallback contributors")
        Seq(bind[ContributorsSummariser].to[OfflineContributorsSummariser])
    }

  }
}


