package services.github

import javax.inject.{Singleton, Inject}

import akka.actor.ActorSystem
import com.google.inject.name.Named
import models.github._
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

trait ContributorsSummariser {
  /**
   * Fetch and summarise the contributors from GitHub.
   */
  def fetchContributors: Future[Contributors]
}

class DefaultContributorsSummariser @Inject() (gitHub: GitHub, config: GitHubConfig)(implicit ec: ExecutionContext) extends ContributorsSummariser {

  /**
   * Fetch all the committers. These are people that have commit access to the play repo.
   *
   * For committers, we don't just load the basic user details, we load the full user details, which includes their
   * name.
   */
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
      contributors <- Future.traverse(contributorRepos)(gitHub.fetchRepoContributors)
    } yield {

      // All contributors and the number of contributions.
      // Since we fetch contributors by repositories, contributors may appear multiple times, so group by contributor,
      // and sum their contributions.
      val contributorContributions: Seq[(GitHubUser, Int)] = contributors
        .flatten
        .groupBy(_._1.id)
        .collect {
        case ((_, contributions @ ((user, _) :: _))) =>
          user -> contributions.map(_._2).sum
      }.toSeq

      // Sort by contributions in reverse.
      contributorContributions.sortBy(_._2)
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

      // Use the ordering from contributors for the ordering of the committers and members
      val orderedCommitters = contributors.flatMap(ghu => committers.filter(_.id == ghu.id))
      val orderedMembers = contributors.flatMap(ghu => filteredMembers.filter(_.id == ghu.id))

      Contributors(orderedCommitters, orderedMembers, filteredContributors)
    }
  }
}

@Singleton
class CachingContributorsSummariser @Inject() (actorSystem: ActorSystem,
                                               @Named("gitHubContributorsSummariser") delegate: ContributorsSummariser)(implicit ec: ExecutionContext) extends ContributorsSummariser {
  private val log = LoggerFactory.getLogger(classOf[CachingContributorsSummariser])

  @volatile private var contributors: Contributors = FallbackContributors.contributors

  actorSystem.scheduler.schedule(0.seconds, 24.hours) {
    delegate.fetchContributors.onComplete {
      case Failure(t) => log.error("Unable to load contributors from GitHub", t)
      case Success(cs) =>
        if (contributors != cs) {
          val count = contributors.committers.size + contributors.playOrganisation.size + contributors.contributors.size
          log.info(s"Loaded $count contributors for GitHub")
        }
        contributors = cs
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


