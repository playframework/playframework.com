package services

/*
import javax.inject.Inject

import com.typesafe.conductr.bundlelib.scala.{LocationCache, URI}
import com.typesafe.conductr.lib.play.api.ConnectionContext
import com.typesafe.dynamicdatasource.DynamicDataSource

import scala.beans.BeanProperty
import scala.concurrent.Future

class LocationServiceDataSource @Inject() (implicit connectionContext:ConnectionContext) extends DynamicDataSource {
  @BeanProperty var localHost: String = _
  @BeanProperty var localPort: Int = _

  // we want akka one specificially
  // we want to use the streaming support from akka
  import com.typesafe.conductr.bundlelib.akka.LocationService

  private val cache = LocationCache()


  override def isWrapperFor(iface: Class[_]): Boolean =
    iface.isInstance(this)

  override def lookup(serviceName: String): Future[Option[(String, Int)]] =
    LocationService.lookup(serviceName, URI(s"tcp://$localHost:$localPort"), cache).map {
      case Some(uri) =>
        Some((uri.getHost, uri.getPort))
      case None =>
        None
    }
}
*/
