package services.github

import models.github._
import org.specs2.mock.Mockito
import play.api.test._

import scala.concurrent.Future

object ContributorsSummariserSpec extends PlaySpecification with Mockito {
  val config = GitHubConfig("", "", "playframework", Seq("Owners", "Developers"))
  val org = Organisation(0, "playframework", "", "", "")
  val ownersTeam = Team(0, "Owners", "", "")
  val collaborators = Team(1, "Collaborators", "", "")
  val playRepo = Repository(0, "playframework", "playframework/playframework", false, "")
  val twirlRepo = Repository(1, "twirl", "playframework/twirl", false, "")
  val forkRepo = Repository(2, "fork", "playframework/fork", true, "")
  val owner = GitHubUser(0, "owner", "", "", "", None)
  val ownerDetails = GitHubUser(0, "owner", "", "", "", Some("Mr Owner"))
  val orgMember = GitHubUser(1, "orgMember", "", "", "", None)
  val contributor1 = GitHubUser(2, "contributor1", "", "", "", None)
  val contributor2 = GitHubUser(3, "contributor2", "", "", "", None)
  val contributor3 = GitHubUser(4, "contributor3", "", "", "", None)

  "Contributors summariser" should {
    "only include committer teams" in run { (gh, contributors) =>
      there was no(gh).fetchTeamMembers(collaborators)
    }
    "fetch details of committers" in run { (gh, contributors) =>
      contributors.committers must_== Seq(ownerDetails)
    }
    "fetch organisation members" in run { (gh, contributors) =>
      contributors.playOrganisation must contain(orgMember)
    }
    "exclude committers from organisation members" in run { (gh, contributors) =>
      contributors.playOrganisation must not contain owner
    }
    "fetch contributors" in run { (gh, contributors) =>
      contributors.contributors must contain(contributor1)
      contributors.contributors must contain(contributor2)
      contributors.contributors must contain(contributor3)
    }
    "exclude committers from contributors" in run { (gh, contributors) =>
      contributors.contributors must not contain owner
    }
    "exclude organisation members from contributors" in run { (gh, contributors) =>
      contributors.contributors must not contain orgMember
    }
    "sort contributors by number of contributions" in run { (gh, contributors) =>
      contributors.contributors must contain(exactly(contributor1, contributor2, contributor3))
    }
    "not fetch forked repositories" in run { (gh, contributors) =>
      there was no(gh).fetchRepoContributors(forkRepo)
    }
  }

  def run[T](block: (GitHub, Contributors) => T) = {
    val gh = mock[GitHub]
    gh.fetchOrganisation("playframework") returns f(org)
    gh.fetchOrganisationTeams(org) returns f(Seq(ownersTeam, collaborators))
    gh.fetchTeamMembers(ownersTeam) returns f(Seq(owner))
    gh.fetchUserDetails(owner) returns f(ownerDetails)

    gh.fetchOrganisationMembers(org) returns f(Seq(owner, orgMember))

    gh.fetchOrganisationRepos(org) returns f(Seq(playRepo, twirlRepo, forkRepo))
    gh.fetchRepoContributors(playRepo) returns f(Seq(contributor2 -> 4, orgMember -> 10, contributor1 -> 3, owner -> 20))
    gh.fetchRepoContributors(twirlRepo) returns f(Seq(contributor1 -> 3, contributor3 -> 1))

    val contributors = await(new DefaultContributorsSummariser(gh, config).fetchContributors)
    block(gh, contributors)
  }

  def f[T](t: T) = Future.successful(t)
}
