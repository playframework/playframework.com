package services.opencollective

import models.opencollective.OpenCollectiveMember
import play.api.libs.json.Reads
import play.api.libs.ws.WSClient
import play.api.libs.ws.WSResponse

import jakarta.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

case class OpenCollectiveConfig(
    restApiUrl: String,
    slug: String,
)

/**
 * Interface to making remote calls on OpenCollective
 */
trait OpenCollective {

  /**
   * Get all the members, no matter if backer, admin or host.
   */
  def fetchMembers(): Future[Seq[OpenCollectiveMember]]

}

class DefaultOpenCollective @Inject() (ws: WSClient, config: OpenCollectiveConfig)(using
    ec: ExecutionContext,
) extends OpenCollective {

  private def load[T: Reads](path: String) = {
    val url = if (path.matches("https?://.*")) path else s"${config.restApiUrl}/${config.slug}/" + path
    ws.url(url).get().map { response =>
      checkSuccess(response).json.as[Seq[T]]
    }
  }

  private def responseFailure(response: WSResponse): Exception = response.status match {
    case 403 =>
      new RuntimeException("Request forbidden: " + response.body)
    case _ => new RuntimeException("Request failed with " + response.status + " " + response.statusText)
  }

  private def checkSuccess(response: WSResponse): WSResponse = response.status match {
    case ok if ok < 300 => response
    case _              => throw responseFailure(response)
  }

  override def fetchMembers(): Future[Seq[OpenCollectiveMember]] =
    load[OpenCollectiveMember]("members/all.json")
}
