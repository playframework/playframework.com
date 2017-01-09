package actors

import javax.inject.Inject

import akka.actor.Actor
import akka.pattern.pipe
import models.ActivatorRelease
import play.api.{Configuration, Logger}
import play.api.libs.ws.WSClient
import scala.concurrent.duration._

object ActivatorReleaseActor {
  case object GetVersion
  case object Tick
}

class ActivatorReleaseActor @Inject() (ws: WSClient, configuration: Configuration) extends Actor {

  import ActivatorReleaseActor._
  import context.dispatcher

  private val activatorUrl = configuration.getOptional[String]("activator.latest-url")
  private val schedule = activatorUrl.map { _ =>
    context.system.scheduler.schedule(1.second, 10.minutes, self, Tick)
  }

  override def postStop() = {
    schedule.foreach(_.cancel())
  }

  def receive = withRelease(
    ActivatorRelease(
      version = "(unknown)",
      url = "https://lightbend.com/platform/getstarted",
      miniUrl = "https://lightbend.com/platform/getstarted",
      size = "???M",
      miniSize = "?M",
      akkaVersion = "(unknown)",
      playVersion = "(unknown)",
      scalaVersion = "(unknown)"
    )
  )
  
  def withRelease(release: ActivatorRelease): Receive = {
    case GetVersion => sender() ! release
    case newRelease: ActivatorRelease =>
      if (newRelease != release) {
        Logger.info("Got activator release " + newRelease)
        context.become(withRelease(newRelease))
      }
    case Tick =>
      activatorUrl.foreach { url =>
        ws.url(url).get().map { response =>
          response.json.as[ActivatorRelease]
        } pipeTo self
      }
  }

}
