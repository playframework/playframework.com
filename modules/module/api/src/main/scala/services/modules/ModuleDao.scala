package services.modules

import models.modules._

/**
 * DAO for accessing modules
 */
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

