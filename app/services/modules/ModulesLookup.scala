package services.modules

import java.io.File
import java.util.zip.ZipFile
import javax.inject.Inject

import com.google.inject.ImplementedBy
import play.api.Environment
import utils.Textile

import scala.io.Source

/**
 * Provides utilities for looking up modules for download and interrogation
 */
@ImplementedBy(classOf[FilesystemModulesLookup])
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

class FilesystemModulesLookup @Inject()(environment: Environment) extends ModulesLookup {

  def findModule(name: String, version: String) =
    environment.getExistingFile("data/modules/" + name + "-" + version + ".zip")

  def loadModuleDocumentation(name: String, version: String, page: String) =
    loadFileFromModule(name, version, "documentation/manual/" + page + ".textile")
      .map(Textile.toHTML)

  def findDependencies(name: String, version: String) =
    loadFileFromModule(name, version, "conf/dependencies.yml")

  private def loadFileFromModule(name: String, version: String, file: String): Option[String] = {
    findModule(name, version).flatMap { zipFile =>
      val zip = new ZipFile(zipFile)
      try {
        Option(zip.getEntry(file)).map { entry =>
          Source.fromInputStream(zip.getInputStream(entry)).mkString
        }
      } finally {
        zip.close()
      }
    }
  }
}
