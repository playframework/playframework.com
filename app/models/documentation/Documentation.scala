package models.documentation

import java.util.Locale

import org.slf4j.LoggerFactory
import play.api.i18n.Lang
import play.doc.PlayDoc
import utils.{ExtendedFileRepository, PlayGitRepository}

import scala.util.control.NonFatal

case class Documentation(default: Translation, defaultLang: Lang, translations: Map[Lang, Translation]) {

  lazy val allLangs: List[Lang] = defaultLang :: translations.keys.toList.sortBy(_.toString)

}

case class Translation(availableVersions: List[TranslationVersion], repo: PlayGitRepository, source: Option[String]) {

  lazy val byVersion: Map[Version, TranslationVersion] = availableVersions.map(v => v.version -> v).toMap

  /**
   * The documentation versions that we will display to read from, in the order that they should be displayed
   * in the drop down list
   */
  lazy val displayVersions: List[Version] = {
    calculateDisplayVersions(availableVersions.map(_.version))
  }

  /**
   * Calculate the available versions, using the following policy:
   *
   * The most recent development version, every minor version of the current major version,
   * and the most recent patch version of every past major version
   */
  private def calculateDisplayVersions(versions: List[Version]): List[Version] = {
    versions.find(_.versionType.isStable).map { currentStable =>
      val mostRecentDevelopment = Version.findMostRecentDevelopment(versions, currentStable)
      val currentPatchVersions = Version.findCurrentPatchVersions(versions, currentStable)
      val previousMostRecentVersions = Version.findPreviousMostRecentVersions(versions, currentStable)
      // Ensure that the most recent Play 1 version, even if it's an RC, is in there
      val mostRecent1 = versions.find(_.era == 1).toSeq

      (List[Version]() ++ mostRecentDevelopment ++ currentPatchVersions ++ previousMostRecentVersions ++ mostRecent1).distinct.sorted.reverse
    } getOrElse versions
  }

  /**
   * The default version to render
   */
  lazy val defaultVersion: Option[Version] = Version.findDefaultVersion(displayVersions)

}

/**
 * A version of a particular translation.
 * 
 * @param version The version
 * @param repo The file repository for the this version
 * @param playDoc The play doc instance that can render the documentation for this version
 * @param cacheId An etag value that uniquely identifies this version
 * @param symbolicName The git symbolic name
 */
case class TranslationVersion(version: Version, repo: ExtendedFileRepository, playDoc: PlayDoc, cacheId: String, symbolicName: String)

/**
 * Release types.  Primary use is to identify which versions are stable, and how to sort them.
 */
sealed abstract class VersionType(val name: String, val order: Int) extends Ordered[VersionType] {
  val number = 0
  def compare(that: VersionType) = {
    val compare1 = order - that.order
    if (compare1 != 0) compare1 else number - that.number
  }
  val isStable = false
  val isLatest = false
}
case object Release extends VersionType("release", 10) {
  override val isStable = true
}
case object Latest extends VersionType("x", 100) {
  override val isLatest = true
}
case class ReleaseCandidate(override val number: Int) extends VersionType("rc", 9)
case class Milestone(override val number: Int) extends VersionType("m", 8)

/**
 * A version.  Primary use is for sorting.
 */
case class Version(name: String, era: Int, major: Int, minor: Int, patch: Int, versionType: VersionType) extends Ordered[Version] {
  def sameMajor(that: Version) = era == that.era && major == that.major
  def earlierMajorThan(that: Version) = {
    if (era < that.era) true
    else if (era > that.era) false
    else major < that.major
  }
  def compare(that: Version) = {
    (era, major, minor, patch, versionType).compareTo(that.era, that.major, that.minor, that.patch, that.versionType)
  }
  override def toString = name
}

object Version {
  private val log = LoggerFactory.getLogger(classOf[Version])

  def findMostRecentDevelopment(versions: Seq[Version], currentStable: Version): Seq[Version] = {
    versions.filter(!_.versionType.isStable).filter(_ > currentStable).take(2)
  }

  def findCurrentPatchVersions(versions: Seq[Version], currentStable: Version): Seq[Version] = {
    versions.filter(v => v.sameMajor(currentStable) && (v.versionType.isStable || v.versionType.isLatest))
  }

  def findPreviousMostRecentVersions(versions: Seq[Version], currentStable: Version) = {
    versions
      .filter(v => (v.versionType.isStable || v.versionType.isLatest) && v.earlierMajorThan(currentStable))
      .foldLeft(List[Version]()) { (vs, v) =>
        if (!vs.exists(_.sameMajor(v))) vs :+ v else vs
      }
  }

  /**
    * Finds the most recent stable version, and then the latest version that matches that
    *
    * @param displayVersions the list of display versions.
    * @return the default version.
    */
  def findDefaultVersion(displayVersions: Seq[Version]): Option[Version] = {
      displayVersions.find(_.versionType.isStable).flatMap { stable =>
        displayVersions.find(v => v.sameMajor(stable) && (v.versionType.isLatest || v.versionType.isStable))
      }.orElse(displayVersions.headOption)
  }

  def parse(name: String): Option[Version] = {
    try {
      val splitted = name.split("\\.", 4)
      val splitLast = splitted(splitted.length - 1).split("-", 2)
      val splittedFull = splitted.dropRight(1) ++ splitLast

      // Convenience some version constructor
      def v(era: Int, major: Int, minor: Int = 0, patch: Int = 0, versionType: VersionType = Release) =
        Some(Version(name, era, major, minor, patch, versionType))

      // Integer extractor object
      object I {
        def unapply(s: String): Option[Int] = {
          try {
            Some(s.toInt)
          } catch {
            case NonFatal(t) => None
          }
        }
      }

      // Version type extractor object
      object T {
        def unapply(s: String): Option[VersionType] = {
          val vt = s.takeWhile(_.isLetter)
          val vtn = s.dropWhile(!_.isDigit)
          val n = if (vtn.isEmpty) 0 else vtn.toInt
          vt.toLowerCase(Locale.ENGLISH) match {
            case "rc" => Some(new ReleaseCandidate(n))
            case "m" => Some(new Milestone(n))
            case "snapshot" => Some(Latest)
            case unknown => None
          }
        }
      }

      splittedFull match {
        case Array(I(era), I(major))                            => v(era, major)
        case Array(I(era), I(major), I(minor))                  => v(era, major, minor)
        case Array(I(era), I(major), "x")                       => v(era, major, 9999, 0, Latest)
        case Array(I(era), I(major), T(vt))                     => v(era, major, 0, 0, vt)
        case Array(I(era), I(major), I(minor), I(patch))        => v(era, major, minor, patch)
        case Array(I(era), I(major), I(minor), "x")             => v(era, major, minor, 9999, Latest)
        case Array(I(era), I(major), I(minor), T(vt))           => v(era, major, minor, 0, vt)
        case Array(I(era), I(major), I(minor), I(patch), T(vt)) => v(era, major, minor, patch, vt)
        case _ => None
      }
    } catch {
      case NonFatal(e) =>
        log.warn("Error encountered while trying to parse version: " + name, e)
        None
    }
  }


}
