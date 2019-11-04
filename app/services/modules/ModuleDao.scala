package services.modules

import javax.inject.Inject
import javax.inject.Singleton

import com.google.inject.ImplementedBy
import models.modules._

import scala.language.postfixOps

/**
 * DAO for accessing modules
 */
@ImplementedBy(classOf[DbModuleDao])
trait ModuleDao {

  /**
   * Find all the modules and all releases of the modules
   */
  def findEverything(): Seq[(Module, Seq[Release])]

  /**
   * Find all modules that have a name like the given keyword
   */
  def findAll(keyword: String = ""): Seq[Module]

  /**
   * Find the given module and all its releases
   */
  def findById(name: String): Option[(Module, Seq[Release])]
}

@Singleton
class DbModuleDao @Inject() extends ModuleDao {

  private val modules: Map[ModuleId, Module] = InMemDatabase.rawModules.toMap
  // not a Map because ModuleId can be duplicate in this Seq
  private val moduleReleases: Seq[(ModuleId, Release)] = InMemDatabase.rawReleases
  private val releasesByModuleId: Map[ModuleId, Seq[Release]] =
    moduleReleases.groupBy(_._1).view.mapValues(_.map(_._2)).toMap
  // left join. Some modules may not have a release.
  private val everything: Seq[(Module, Seq[Release])] = modules.map {
    case (id, mod) => mod -> releasesByModuleId.getOrElse(id, Seq.empty[Release]).sortBy(_.date)
  }.toSeq

  def findEverything(): Seq[(Module, Seq[Release])] = everything

  // find all module whose name contains `keyword`
  def findAll(keyword: String = ""): Seq[Module] =
    modules.values.filter(_.fullname.toLowerCase.contains(keyword.toLowerCase())).toSeq

  def findById(name: String): Option[(Module, Seq[Release])] =
    everything.find(_._1.name == name).map {
      case (mod, releases) => (mod, releases.sortBy(_.date).reverse)
    }

}
