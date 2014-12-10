package services.github

import javax.inject.Inject

import akka.actor.ActorSystem
import com.google.inject.name.Named
import models.github._
import play.api.Logger

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import play.api.libs.concurrent.Execution.Implicits._

trait ContributorsSummariser {
  /**
   * Fetch and summarise the contributors from GitHub.
   */
  def fetchContributors: Future[Contributors]
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


