package views.html.documentation

import controllers.documentation.ReverseRouter
import models.documentation.Milestone
import models.documentation.ReleaseCandidate
import models.documentation.TranslationContext
import models.documentation.Version
import play.api.i18n.Lang
import play.api.i18n.MessagesApi
import play.twirl.api.Html

object Helpers {

  def isNotMostRecentVersion(implicit context: TranslationContext): Boolean = {
    !context.version.equals(latestCurrent)
  }

  def displayVersionMessage(
      page: String,
  )(implicit messagesApi: MessagesApi, context: TranslationContext, reverseRouter: ReverseRouter): Html = {
    implicit val alternateLang = context.alternateLang
    implicit val lang          = context.alternateLang.getOrElse(Lang.defaultLang)
    val version                = context.version.get
    if (isDevelopmentVersion(version)) {
      upgrade(unstableMessage(link(version, page)), page)
    } else {
      val versionType = version.versionType
      if (versionType.isLatest) {
        // If it's the latest, then we've got 2.3.x or 2.4.x, a series release.
        // If it's 2.5.x, we never get here in the first place.
        upgrade(oldLatestMessage(link(version, page)), page)
      } else {
        val mostCurrentVersion = latestCurrent.get
        if (version.sameMajor(mostCurrentVersion)) {
          upgrade(currentReleaseMessage(link(version, page)), page)
        } else {
          // We have a specific release -- 2.3.4 or 2.4.2, a specific release in the series.
          val sameMajor = latestCompatible.get
          upgrade(oldReleaseMessage(link(version, page), link(sameMajor, page)), page)
        }
      }
    }
  }

  private def upgrade(
      originalHtml: Html,
      page: String,
  )(implicit messagesApi: MessagesApi, context: TranslationContext, reverseRouter: ReverseRouter): Html = {
    import scala.collection.immutable.Seq
    new Html(Seq(originalHtml, Html(" "), displayUpgradeMessage(page)))
  }

  // 2.6.0
  private def unstableMessage(specificLink: Html)(implicit messagesApi: MessagesApi, lang: Lang) = {
    // documentation.unstable.message=You are viewing the documentation for the {0} development release.
    Html(messagesApi("documentation.unstable.message", specificLink.toString()))
  }

  // Current release 2.5.0.  (Displayed because latest is 2.5.x)
  private def currentReleaseMessage(specificLink: Html)(implicit messagesApi: MessagesApi, lang: Lang): Html = {
    // You are viewing the documentation for the {0} release.
    Html(messagesApi("documentation.current.release.message", specificLink.toString()))
  }

  // Used to point people to 2.5.x
  private def currentLatestMessage(seriesLink: Html)(implicit messagesApi: MessagesApi, lang: Lang): Html = {
    // The latest stable release series is {0}.
    Html(messagesApi("documentation.current.latest.message", seriesLink.toString()))
  }

  // 2.3.2
  private def oldReleaseMessage(
      specificLink: Html,
      seriesLink: Html,
  )(implicit messagesApi: MessagesApi, lang: Lang): Html = {
    // You are viewing the documentation for the {0} release in the {1} series of releases.
    Html(messagesApi("documentation.old.release.message", specificLink.toString(), seriesLink.toString()))
  }

  // 2.3.x
  private def oldLatestMessage(seriesLink: Html)(implicit messagesApi: MessagesApi, lang: Lang): Html = {
    // You are viewing the documentation for the {0} release series.
    Html(messagesApi("documentation.old.latest.message", seriesLink.toString()))
  }

  private def link(
      version: Version,
      page: String,
  )(implicit alternateLang: Option[Lang], reverseRouter: ReverseRouter): Html = {
    val url = reverseRouter.page(alternateLang, version.toString, page)
    Html(s"""<a href="$url">${version.toString}</a>""")
  }

  private def displayUpgradeMessage(
      page: String,
  )(implicit messagesApi: MessagesApi, context: TranslationContext, reverseRouter: ReverseRouter): Html = {
    val version                = latestCurrent.get
    implicit val alternateLang = context.alternateLang
    implicit val lang          = context.alternateLang.getOrElse(Lang.defaultLang)
    currentLatestMessage(link(version, page))
  }

  /** Returns the most current version, i.e. 2.6.x. */
  def latestCurrent(implicit context: TranslationContext): Option[Version] = {
    Version.findDefaultVersion(context.displayVersions)
  }

  /** Returns the latest compatible version for the context.version, i.e. 2.3.0 will return 2.3.x */
  private def latestCompatible(implicit context: TranslationContext): Option[Version] = {
    context.version.flatMap(v => Version.findCurrentPatchVersions(context.displayVersions, v).headOption)
  }

  private def isDevelopmentVersion(version: Version)(implicit context: TranslationContext): Boolean = {
    version.versionType match {
      case rc: ReleaseCandidate                                  => true
      case m: Milestone                                          => true
      case series if latestCurrent.get.earlierMajorThan(version) => true // hack
      case other                                                 => false
    }
  }

}
