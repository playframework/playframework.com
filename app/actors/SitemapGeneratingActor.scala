package actors

import akka.actor.Actor
import models.documentation.Sitemap
import models.documentation.Documentation

object SitemapGeneratingActor {

  case class GenerateSitemap(documentation: Documentation)

}

class SitemapGeneratingActor extends Actor {
  import SitemapGeneratingActor._

  def receive = {
    case GenerateSitemap(documentation) =>
      // this will perform IO as it has to look stuff up in git repos
      val sitemap = Sitemap(documentation)
      sender ! sitemap
  }
}
