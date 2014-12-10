package services.github

import java.io.File

import org.apache.commons.io.IOUtils
import play.api.libs.ws.WSClient
import play.api.libs.ws.ning.NingWSClient
import play.api.{Environment, ApplicationLoader, BuiltInComponentsFromContext}
import play.api.http.{HttpErrorHandler, DefaultHttpErrorHandler}
import play.api.mvc.{Results, Action, Handler, RequestHeader}
import play.api.test._
import play.core.ApplicationProvider
import play.core.server.{ServerConfig, NettyServer}
import utils.routing._

import scala.util.Success

object GitHubSpec extends PlaySpecification {

  "The GitHub service" should {
    "allow getting an organisation" in withGitHub { gh =>
      val org = await(gh.fetchOrganisation("playframework"))
      org.login must_== "playframework"
    }
    "allow getting organisation members" in withGitHub { gh =>
      val org = await(gh.fetchOrganisation("playframework"))
      val members = await(gh.fetchOrganisationMembers(org))
      members must haveSize(2)
      members(0).login must_== "guillaumebort"
    }
    "allow getting organisation teams" in withGitHub { gh =>
      val org = await(gh.fetchOrganisation("playframework"))
      val teams = await(gh.fetchOrganisationTeams(org))
      teams must haveSize(2)
      teams(0).name must_== "Owners"
    }
    "allow getting organisation repos" in withGitHub { gh =>
      val org = await(gh.fetchOrganisation("playframework"))
      val repos = await(gh.fetchOrganisationRepos(org))
      repos must haveSize(2)
      repos(0).name must_== "playframework"
      repos(0).fullName must_== "playframework/playframework"
    }
    "allow getting team members" in withGitHub { gh =>
      val org = await(gh.fetchOrganisation("playframework"))
      val teams = await(gh.fetchOrganisationTeams(org))
      val members = await(gh.fetchTeamMembers(teams(0)))
      members must haveSize(2)
      members(0).login must_== "guillaumebort"
    }
    "allow getting paged contributors" in withGitHub { gh =>
      val org = await(gh.fetchOrganisation("playframework"))
      val repos = await(gh.fetchOrganisationRepos(org))
      val contributors = await(gh.fetchRepoContributors(repos(0)))
      // There are actually two pages of contributors, this test tests paging
      contributors must haveSize(4)
      contributors(0)._1.login must_== "jroper"
      contributors(0)._2 must_== 1193
      contributors(3)._1.login must_== "huntc"
    }
    "allow getting user details" in withGitHub { gh =>
      val org = await(gh.fetchOrganisation("playframework"))
      val members = await(gh.fetchOrganisationMembers(org))
      val user = await(gh.fetchUserDetails(members(0)))
      user.name must beSome("James Roper")
    }
  }

  def withGitHub[T](block: GitHub => T) = Server.withClientAndServer(mockGitHub) { (ws, port) =>
    val gitHub = new DefaultGitHub(ws, GitHubConfig("token", "http://localhost:" + port, "playframework", Nil))
    block(gitHub)
  }

  def action(jsonFile: String, headers: (String, String)*) = Action {
    val json = IOUtils.toString(this.getClass.getClassLoader.getResource("github/" + jsonFile))
    val fixed = json.replaceAll("https://api.github.com", "http://localhost:9001")
    Results.Ok(fixed).as("application/json").withHeaders(headers: _*)
  }
  val mockGitHub: Int => PartialFunction[RequestHeader, Handler] = port => {
    case Route("GET", route"/orgs/${_}") => action("org.json")
    case Route("GET", route"/orgs/${_}/members") => action("orgMembers.json")
    case Route("GET", route"/orgs/${_}/teams") => action("teams.json")
    case Route("GET", route"/orgs/${_}/repos") => action("repos.json")
    case Route("GET", route"/teams/${_}/members") => action("teamMembers.json")
    case rh @ Route("GET", route"/repos/${_}/${_}/contributors" | route"/repositories/${_}/contributors") => rh.getQueryString("page") match {
      case None => action("contributors1.json",
        "Link" -> s"""<http://localhost:$port/repositories/2340549/contributors?per_page=2&page=2>; rel="next""""
      )
      case Some("2") => action("contributors2.json")
    }
    case Route("GET", route"/users/${_}") => action("user.json")
  }
}

// Everything below here can/should be brought into Play in some form

object Server {
  def withClientAndServer[T](s: Int => PartialFunction[RequestHeader, Handler])(block: (WSClient, Int) => T) = {
    val server = withRoutes(9001)(s(9001))
    try {
      val client = NingWSClient()
      try {
        block(client, 9001)
      } finally {
        client.close()
      }
    } finally {
      server.stop()
    }
  }

  def withRoutes(port: Int)(suppliedRoutes: PartialFunction[RequestHeader, Handler]): play.core.server.Server = {
    val application = new BuiltInComponentsFromContext(ApplicationLoader.createContext(Environment.simple())) {
      def routes = Router.routes(suppliedRoutes)
    }.application

    val appProvider = new ApplicationProvider {
      def get = Success(application)
      def path = new File(".")
    }

    new NettyServer(ServerConfig(new File("."), Some(port), None, properties = System.getProperties), appProvider)
  }
}

object Router {
  def routes(routes: PartialFunction[RequestHeader, Handler]): SimpleRoutes = Router.routes(DefaultHttpErrorHandler)(routes)
  def routes(errorHandler: HttpErrorHandler)(routes: PartialFunction[RequestHeader, Handler]): SimpleRoutes = new SimpleRoutes(routes, "/", errorHandler)

  class SimpleRoutes(suppliedRoutes: PartialFunction[RequestHeader, Handler],
                     prefix: String, val errorHandler: HttpErrorHandler) extends play.core.Router.Routes {
    def routes = {
      if (prefix == "/") {
        suppliedRoutes
      } else {
        val p = if (prefix.endsWith("/")) prefix else prefix + "/"
        val prefixed: PartialFunction[RequestHeader, RequestHeader] = {
          case rh: RequestHeader if rh.path.startsWith(p) => rh.copy(path = rh.path.drop(p.length - 1))
        }
        Function.unlift(prefixed.lift.andThen(_.flatMap(suppliedRoutes.lift)))
      }
    }
    def documentation = Nil
    def withPrefix(prefix: String) = new SimpleRoutes(suppliedRoutes, prefix, errorHandler)
  }
}