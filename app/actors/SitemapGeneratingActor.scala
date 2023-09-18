package actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
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
