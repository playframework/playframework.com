package services.github

import jakarta.inject.Inject

import com.damnhandy.uri.template.UriTemplate
import play.api.http.HeaderNames
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.ws._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import models.github._

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

case class GitHubConfig(
    accessToken: String,
    gitHubApiUrl: String,
    organisation: String,
    committerTeams: Seq[String],
)

class DefaultGitHub @Inject() (ws: WSClient, config: GitHubConfig)(using ec: ExecutionContext) extends GitHub {

  private def authCall(url: String) = {
    ws.url(url).withHttpHeaders(HeaderNames.AUTHORIZATION -> ("token " + config.accessToken))
  }

  private def load[T: Reads](path: String) = {
    val url = if (path.matches("https?://.*")) path else config.gitHubApiUrl + path
    authCall(url).get().map { response =>
      checkSuccess(response).json.as[T]
    }
  }

  object NextLink {
    val ParseNext                                     = """.*<([^>]+)>;\s*rel="?next"?.*""".r
    def unapply(response: WSResponse): Option[String] = {
      response.header("Link").collect { case ParseNext(next) =>
        next
      }
    }
  }

  private def responseFailure(response: WSResponse): Exception = response.status match {
    case 403 =>
      new RuntimeException("Request forbidden, GitHub quota rate limit is probably exceeded: " + response.body)
    case error => new RuntimeException("Request failed with " + response.status + " " + response.statusText)
  }

  private def checkSuccess(response: WSResponse): WSResponse = response.status match {
    case ok if ok < 300 => response
    case error          => throw responseFailure(response)
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
        case response @ NextLink(next)    =>
          for {
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
      implicitly[Reads[GitHubUser]].and((__ \ "contributions").read[Int]).tupled,
    )

  def fetchOrganisationRepos(organisation: Organisation) =
    loadWithPaging[Repository](expand(organisation.reposUrl))
}
