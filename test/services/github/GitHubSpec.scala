package services.github

import org.specs2.mutable.Specification
import play.api.http.{DefaultFileMimeTypesProvider, FileMimeTypesConfiguration}

import play.api.mvc.Results._
import play.api.mvc.Action
import play.api.test.{ WsTestClient, PlaySpecification }
import play.core.server.Server
import play.api.routing.sird.{ GET => Get, _ }

import scala.concurrent.ExecutionContext.Implicits.global

object GitHubSpec extends PlaySpecification {

  implicit val fileMimeTypes = new DefaultFileMimeTypesProvider(FileMimeTypesConfiguration(Map("json" -> "application/json"))).get

  "The GitHub service" should {
    "allow getting an organisation" in withGitHub { gh =>
      val org = await(gh.fetchOrganisation("playframework"))
      org.login must_== "playframework"
    }
    "allow getting organisation members" in withGitHub { gh =>
      val org = await(gh.fetchOrganisation("playframework"))
      val members = await(gh.fetchOrganisationMembers(org))
      members must haveSize(2)
      members.head.login must_== "guillaumebort"
    }
    "allow getting organisation teams" in withGitHub { gh =>
      val org = await(gh.fetchOrganisation("playframework"))
      val teams = await(gh.fetchOrganisationTeams(org))
      teams must haveSize(2)
      teams.head.name must_== "Owners"
    }
    "allow getting organisation repos" in withGitHub { gh =>
      val org = await(gh.fetchOrganisation("playframework"))
      val repos = await(gh.fetchOrganisationRepos(org))
      repos must haveSize(2)
      repos.head.name must_== "playframework"
      repos.head.fullName must_== "playframework/playframework"
    }
    "allow getting team members" in withGitHub { gh =>
      val org = await(gh.fetchOrganisation("playframework"))
      val teams = await(gh.fetchOrganisationTeams(org))
      val members = await(gh.fetchTeamMembers(teams.head))
      members must haveSize(2)
      members.head.login must_== "guillaumebort"
    }
    "allow getting paged contributors" in withGitHub { gh =>
      val org = await(gh.fetchOrganisation("playframework"))
      val repos = await(gh.fetchOrganisationRepos(org))
      val contributors = await(gh.fetchRepoContributors(repos.head))
      // There are actually two pages of contributors, this test tests paging
      contributors must haveSize(4)
      contributors.head._1.login must_== "jroper"
      contributors.head._2 must_== 1193
      contributors(3)._1.login must_== "huntc"
    }
    "allow getting user details" in withGitHub { gh =>
      val org = await(gh.fetchOrganisation("playframework"))
      val members = await(gh.fetchOrganisationMembers(org))
      val user = await(gh.fetchUserDetails(members.head))
      user.name must beSome("James Roper")
    }
  }

  def withGitHub[T](block: GitHub => T) = Server.withRouter() {
    case Get(p"/orgs/${_}") => Action(Ok.sendResource("github/org.json"))
    case Get(p"/orgs/${_}/members") => Action(Ok.sendResource("github/orgMembers.json"))
    case Get(p"/orgs/${_}/teams") => Action(Ok.sendResource("github/teams.json"))
    case Get(p"/orgs/${_}/repos") => Action(Ok.sendResource("github/repos.json"))
    case Get(p"/teams/${_}/members") => Action(Ok.sendResource("github/teamMembers.json"))
    case Get(p"/repos/${_}/${_}/contributors" | p"/repositories/${_}/contributors") => Action { req =>
      req.getQueryString("page") match {
        case None => Ok.sendResource("github/contributors1.json").withHeaders(
          "Link" -> s"""</repositories/2340549/contributors?per_page=2&page=2>; rel="next""""
        )
        case Some("2") => Ok.sendResource("github/contributors2.json")
      }
    }
    case Get(p"/users/${_}") => Action(Ok.sendResource("github/user.json"))
  } { implicit port =>
    WsTestClient.withClient { ws =>
      val gitHub = new DefaultGitHub(ws, GitHubConfig("token", "", "playframework", Nil))
      block(gitHub)
    }
  }
}
