package services.modules

import java.io.File

/**
 * Provides utilities for looking up modules for download and interogation
 */
trait ModulesLookup {

  /**
   * Find the file for the module, if it exists.
   */
  def findModule(name: String, version: String): Option[File]

  /**
   * Load the documentation for the given page of the module, if it exists.
   */
  def loadModuleDocumentation(name: String, version: String, page: String): Option[String]

  /**
   * Find the dependencies yaml file for the given module, if it exists.
   */
  def findDependencies(name: String, version: String): Option[String]
}
