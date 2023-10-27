package actors

import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import models.documentation.Sitemap
import models.documentation.Documentation

object SitemapGeneratingActor {
  final case class GenerateSitemap(documentation: Documentation, replyTo: ActorRef[Sitemap])

  def apply(): Behavior[GenerateSitemap] = Behaviors.receiveMessage {
    case GenerateSitemap(documentation, replyTo) =>
      // this will perform IO as it has to look stuff up in git repos
      val sitemap = Sitemap(documentation)
      replyTo ! sitemap
      Behaviors.same
  }
}
