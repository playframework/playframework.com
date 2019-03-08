package services.github

import play.api.http.DefaultFileMimeTypesProvider
import play.api.http.FileMimeTypesConfiguration
import play.api.mvc.Results._
import play.api.routing.sird.{ GET => Get, _ }
import play.api.test.PlaySpecification
import play.api.test.WsTestClient
import play.core.server.Server

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.Future

object GitHubSpec extends PlaySpecification {

  implicit val fileMimeTypes = new DefaultFileMimeTypesProvider(
    FileMimeTypesConfiguration(Map("json" -> "application/json")),
  ).get

  "The GitHub service" should {
    "allow getting an organisation" in withGitHub { gh =>
      val org = await(gh.fetchOrganisation("playframework"))
      org.login must_== "playframework"
    }
    "allow getting organisation members" in withGitHub { gh =>
      val org     = await(gh.fetchOrganisation("playframework"))
      val members = await(gh.fetchOrganisationMembers(org))
      members must haveSize(2)
      members(0).login must_== "guillaumebort"
    }
    "allow getting organisation teams" in withGitHub { gh =>
      val org   = await(gh.fetchOrganisation("playframework"))
      val teams = await(gh.fetchOrganisationTeams(org))
      teams must haveSize(2)
      teams(0).name must_== "Owners"
    }
    "allow getting organisation repos" in withGitHub { gh =>
      val org   = await(gh.fetchOrganisation("playframework"))
      val repos = await(gh.fetchOrganisationRepos(org))
      repos must haveSize(2)
      repos(0).name must_== "playframework"
      repos(0).fullName must_== "playframework/playframework"
    }
    "allow getting team members" in withGitHub { gh =>
      val org     = await(gh.fetchOrganisation("playframework"))
      val teams   = await(gh.fetchOrganisationTeams(org))
      val members = await(gh.fetchTeamMembers(teams(0)))
      members must haveSize(2)
      members(0).login must_== "guillaumebort"
    }
    "allow getting paged contributors" in withGitHub { gh =>
      val org          = await(gh.fetchOrganisation("playframework"))
      val repos        = await(gh.fetchOrganisationRepos(org))
      val contributors = await(gh.fetchRepoContributors(repos(0)))
      // There are actually two pages of contributors, this test tests paging
      contributors must haveSize(4)
      contributors(0)._1.login must_== "jroper"
      contributors(0)._2 must_== 1193
      contributors(3)._1.login must_== "huntc"
    }
    "allow getting user details" in withGitHub { gh =>
      val org     = await(gh.fetchOrganisation("playframework"))
      val members = await(gh.fetchOrganisationMembers(org))
      val user    = await(gh.fetchUserDetails(members(0)))
      user.name must beSome("James Roper")
    }
  }

  def await[T](future: Future[T]): T = Await.result(future, 10.seconds)

  def withGitHub[T](block: GitHub => T): T =
    Server.withRouterFromComponents() { components =>
      {
        case Get(p"/orgs/${_ }") => components.defaultActionBuilder(Ok.sendResource("github/org.json"))
        case Get(p"/orgs/${_ }/members") =>
          components.defaultActionBuilder(Ok.sendResource("github/orgMembers.json"))
        case Get(p"/orgs/${_ }/teams") => components.defaultActionBuilder(Ok.sendResource("github/teams.json"))
        case Get(p"/orgs/${_ }/repos") => components.defaultActionBuilder(Ok.sendResource("github/repos.json"))
        case Get(p"/teams/${_ }/members") =>
          components.defaultActionBuilder(Ok.sendResource("github/teamMembers.json"))
        case Get(p"/repos/${_ }/${_ }/contributors" | p"/repositories/${_ }/contributors") =>
          components.defaultActionBuilder { req =>
            req.getQueryString("page") match {
              case None =>
                Ok.sendResource("github/contributors1.json")
                  .withHeaders(
                    "Link" -> s"""</repositories/2340549/contributors?per_page=2&page=2>; rel="next"""",
                  )
              case Some(_) => Ok.sendResource("github/contributors2.json")
            }
          }
        case Get(p"/users/${_ }") => components.defaultActionBuilder(Ok.sendResource("github/user.json"))
      }
    } { implicit port =>
      WsTestClient.withClient { ws =>
        val gitHub = new DefaultGitHub(ws, GitHubConfig("token", "", "playframework", Nil))
        block(gitHub)
      }
    }
}
