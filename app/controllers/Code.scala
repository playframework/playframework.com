package controllers

import javax.inject.{ Inject, Singleton }
import play.api._
import play.api.mvc.{Action, Controller}

import play.api.libs.ws._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current

import scala.concurrent._
import collection.SortedMap
import scala.util.{Failure, Success}
import utils.FallbackContributors

object Code {

  private def instance: Code = Play.current.injector.instanceOf[Code]

  def index = instance.index

  // To be a member of a blessed organisation (which is just the Play organisation) you need to have contributed to
  // Play 2 and have at least 10 contributions
  case class Organisation(id: String, name: String, url: String, blessed: Boolean) {
    def canBeMember(contributor: Contributor) = {
      if (blessed) {
        contributor.isPlay2 && contributor.contributions > 10
      } else {
        true
      }
    }
  }
  case class Contributor(url: String, login: String, link: String, gravatar: Option[String], contributions: Int, isPlay2: Boolean)
  case class CoreContributor(url: String, login: String, name: Option[String], link: String, gravatar: Option[String], bio: Option[String])
  case class Contributors(core: Seq[CoreContributor], organisationBased: SortedMap[Organisation, Seq[Contributor]], others: Seq[Contributor])

  implicit val organisationOrdering = Ordering.by[Organisation, String](_.id).reverse

  val organisations = Seq(
    Organisation("playframework", "Play framework", "https://www.playframework.org", true),
    Organisation("zenexity", "Zengularity", "http://www.zengularity.com", false),
    Organisation("typesafehub", "Typesafe", "https://www.typesafe.com", false),
    Organisation("lunatech-labs", "Lunatech Labs", "http://www.lunatech.com", false)
  )

  def fetchContributors(accessToken: String) = instance.fetchContributors(accessToken)

  object NextLink {
    val ParseNext = """.*<([^>]+)>;\s*rel="next".*""".r
    def unapply(response: WSResponse): Option[String] = {
      response.header("Link").collect {
        case ParseNext(next) => next
      }
    }
  }

  private def checkSuccess(response: WSResponse): WSResponse = response.status match {
    case ok if ok < 300 => response
    case error => throw responseFailure(response)
  }

  private def responseFailure(response: WSResponse): Exception = response.status match {
    case 403 => new RuntimeException("Request forbidden, GitHub quota rate limit is probably exceeded: " + response.body)
    case error => new RuntimeException("Request failed with " + response.status + " " +
      response.underlying[com.ning.http.client.Response].getStatusText)
  }

}

@Singleton
class Code @Inject() () extends Controller {

  import Code._

  def index = Action { implicit req =>
    Ok(views.html.code(contributors))
  }

  // -- Fetch team

  @volatile var contributors = FallbackContributors.contributors

  def fetchContributors(accessToken: String) = {

    def authCall(url: String) = {
      WS.url(url).withHeaders(AUTHORIZATION -> ("token " + accessToken))
    }

    def load(path: String) = {
      val url = if (path.matches("https?://.*")) path else "https://api.github.com/" + path
      authCall(url).get()
    }

    /**
     * Loads all pages from a GitHub API endpoint that uses Link headers for paging
     */
    def loadWithPaging[T](path: String)(parser: WSResponse => Seq[T]): Future[Seq[T]] = {
      val url = if (path.matches("https?://.*")) path else "https://api.github.com/" + path + "?per_page=100"
      authCall(url).get().flatMap {
        case notOk if notOk.status >= 300 => throw responseFailure(notOk)
        case response @ NextLink(next) => for {
          nextResults <- loadWithPaging(next)(parser)
        } yield parser(response) ++ nextResults
        case lastResponse => Future.successful(parser(lastResponse))
      }
    }

    def parseContributor(json: JsValue, isPlay2: Boolean) = {
      val login = (json \ "login").as[String]
      Contributor((json \ "url").as[String],
        login,
        "https://github.com/" + login,
        (json \ "gravatar_id").asOpt[String],
        (json \ "contributions").as[Int],
        isPlay2
      )
    }

    def parseContributors(response: WSResponse): Seq[Contributor] = {
      response.json match {
        case JsArray(members) => members.map(json => parseContributor(json, true))
        case _ => Seq()
      }
    }

    def getContributors: Future[Seq[Contributor]] = for {
      play2 <- loadWithPaging("repos/playframework/playframework/contributors")(parseContributors)
      play1 <- loadWithPaging("repos/playframework/play1/contributors")(parseContributors)
    } yield {
      (play1 ++ play2).groupBy(_.login).map { contributor =>
        contributor._2.reduce((a, b) => a.copy(
          contributions = a.contributions + b.contributions,
          isPlay2 = a.isPlay2 || b.isPlay2
        ))
      }.toSeq.sortBy(_.contributions).reverse
    }

    def parseOrganisation(response: WSResponse): Seq[String] = {
      checkSuccess(response).json match {
        case JsArray(members) => members.map(m => (m \ "url").as[String])
        case _ => Nil
      }
    }

    def groupContributorsByOrg(contributors: Seq[Contributor]): Future[Map[Option[Organisation], Seq[Contributor]]] = for {
      orgMembers <- Future.sequence(organisations.map {
        org => loadWithPaging("orgs/" + org.id + "/members")(parseOrganisation)
          .map(members => (org, members.toSet))
      })
    } yield {
      contributors.map { contributor =>
        orgMembers.find { org =>
          org._1.canBeMember(contributor) && org._2.contains(contributor.url)
        }.map(_._1) -> contributor
      }.groupBy(_._1).map(o => o._1 -> o._2.map(_._2))
    }

    def getCoreContributorsDetails(byOrg: Map[Option[Organisation], Seq[Contributor]]): Future[Seq[CoreContributor]] = {
      val coreContributors = byOrg.find(_._1.filter(_.id == "playframework").isDefined).map(_._2).getOrElse(Nil)
      Future.sequence(coreContributors.map(contributor => load(contributor.url).map { response =>
        val json = checkSuccess(response).json
        CoreContributor(contributor.url, contributor.login, (json \ "name").asOpt[String], (json \ "html_url").as[String], contributor.gravatar, (json \ "bio").asOpt[String])
      }))
    }

    val future = for {
      allContributors   <- getContributors
      contributorsByOrg <- groupContributorsByOrg(allContributors)
      coreContributors  <- getCoreContributorsDetails(contributorsByOrg)
    } yield {
      val orgBasedContributors = contributorsByOrg.collect {
        case (Some(org), cs) if org.id != "playframework" => (org, cs.sortBy(_.contributions).reverse)
      }
      contributors = Contributors(
        coreContributors.sortBy(_.name.flatMap(_.split(" ").drop(1).headOption).getOrElse("Z")),
        SortedMap(orgBasedContributors.toSeq :_*),
        contributorsByOrg.get(None).getOrElse(Nil)
      )
    }

    future.onComplete {
      case Failure(t) => Logger.error("Unable to load contributors from GitHub", t)
      case Success(_) =>
        val count = contributors.core.size + contributors.organisationBased.foldLeft(0)((c, o) => c + o._2.size) + contributors.others.size
        Logger.info("Loaded " + count + " contributors for GitHub")
    }
  }

}
