package services.opencollective

import jakarta.inject.Singleton
import jakarta.inject.Inject
import org.apache.pekko.actor.ActorSystem
import com.google.inject.name.Named
import models.opencollective.FallbackMembers
import models.opencollective.OpenCollectiveMember
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Failure
import scala.util.Success

trait MembersSummariser {

  /**
   * Fetch and summarise the members from OpenCollective.
   */
  def fetchMembers: Future[Seq[OpenCollectiveMember]]
}

class DefaultMembersSummariser @Inject() (openCollective: OpenCollective)(using
    ec: ExecutionContext,
) extends MembersSummariser {

  def fetchMembers = {
    for {
      members <- openCollective.fetchMembers()
    } yield {
      val filteredMembers = members.filter(m =>
        m.isActive && m.role == "BACKER" && m.name != "Guest",
      ) // Users with the name "Guest" don't even have a profile picture
      filteredMembers.sortBy(_.totalAmountDonated).reverse
    }
  }
}

@Singleton
class CachingMembersSummariser @Inject() (
    actorSystem: ActorSystem,
    @Named("openCollectiveMembersSummariser") delegate: MembersSummariser,
)(using ec: ExecutionContext)
    extends MembersSummariser {
  private val log = LoggerFactory.getLogger(classOf[CachingMembersSummariser])

  @volatile private var members: Seq[OpenCollectiveMember] = FallbackMembers.members

  actorSystem.scheduler.scheduleWithFixedDelay(0.seconds, 24.hours)(() => {
    delegate.fetchMembers.onComplete {
      case Failure(t)  => log.error("Unable to load members from OpenCollective", t)
      case Success(ms) =>
        if (members != ms) {
          val count = members.size
          log.info("Loaded {} members for OpenCollective", count)
        }
        members = ms
    }
  })

  def fetchMembers = Future.successful(members)
}

/**
 * For use "offline", ie in development, when you don't have access the OpenCollective APi
 */
class OfflineMembersSummariser extends MembersSummariser {
  def fetchMembers = Future.successful(FallbackMembers.members)
}
