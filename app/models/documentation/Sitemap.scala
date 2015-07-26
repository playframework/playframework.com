package models.documentation

import java.nio.file.Paths

import play.doc.{TocTree, Toc, Page, TocPage}

import scala.xml.Node

case class Priority(value: Double) {
  require(value <= Priority.Max, s"Sitemap priority cannot be greater than ${Priority.Max}")
  require(value >= Priority.Min, s"Sitemap priority cannot be less than ${Priority.Min}")
}

object Priority {
  val Max = 1.0
  val Min = 0.1 // Note: Google's docs say 0.1, but sitemaps.org says 0.0
  val Range = Max - Min
  val Default = 0.5

  /**
   * Calculate a given version's priority in the sitemap.
   *
   * Priority is calculated as follows:
   * - priority descends linearly from 1.0 to 0.1 as you go down the list of versions
   * - unstable versions are penalised by subtracting 0.2
   * - minimum priority is bounded to 0.1
   *
   * @param version The Play version of the documentation page
   * @param displayVersions The documentation versions that we will display to read from,
   *                        in the order that they should be displayed in the drop down list
   */
  def apply(version: Version, displayVersions: Seq[Version]): Priority = {
    val maxIndex = displayVersions.length - 1
    if (maxIndex < 1) {
      // avoid divide by zero
      Priority(Default)
    } else {
      val index = displayVersions.zipWithIndex.find(_._1 == version).map(_._2)
      val initialScore = index.map(i => Max - (i * Range / maxIndex)).getOrElse(Default)
      val rescored = if (version.versionType.isStable) initialScore else initialScore - 0.2
      val bounded = rescored max Min
      Priority(bounded)
    }
  }

}

case class SitemapUrl(loc: String, priority: Priority) // TODO links to translations

case class Sitemap(urls: Seq[SitemapUrl]) {

  def toXml: Node = {
    <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
    { urls map { url =>
      <url>
        <loc>{url.loc}</loc>
        <priority>{url.priority.value.formatted("%.2f")}</priority>
      </url>
    }}
    </urlset>
  }

}

object Sitemap {

  private object Play2DocPage {
    def unapply(path: String): Option[String] = {
      Paths.get(path).getFileName.toString match {
        case sidebar if sidebar.startsWith("_") => None
        case markdown if markdown.endsWith(".md") => Some(markdown.dropRight(3))
        case other => None
      }
    }
  }

  private object Play1DocPage {
    def unapply(path: String): Option[String] = {
      Paths.get(path).getFileName.toString match {
        case textile if textile.endsWith(".textile") => Some(textile.dropRight(8))
        case other => None
      }
    }
  }

  private def findPages(tv: TranslationVersion): Seq[String] = {
    def findPagesInToc(toc: TocTree): Seq[String] = toc match {
      case TocPage(page, title) => Seq(page)
      case Toc(name, title, nodes, _) => nodes.flatMap(node => findPagesInToc(node._2))
    }

    // Docs for Play 2.4.0 or newer have a PageIndex with a handy ToC to find the pages for us
    val fromIndex =
      for (pageIndex <- tv.playDoc.pageIndex) yield findPagesInToc(pageIndex.toc)

    // For older versions, we need to find the pages the hard way
    fromIndex getOrElse {
      tv.repo.listAllFilesInPath("manual").collect {
        case Play2DocPage(page) => page
        case Play1DocPage(page) => page
      }
    }
  }

  def apply(documentation: Documentation): Sitemap = {
    val displayVersions = documentation.default.displayVersions

    val locsByVersion: Seq[(Version, Seq[String])] = for {
      version <- displayVersions
      tv <- documentation.default.byVersion.get(version)
      pages = findPages(tv)
      locs = pages.map(page => s"https://www.playframework.com/documentation/$version/$page")
    } yield {
      (version, locs)
    }

    val sitemapUrls = for {
      (version, locs) <- locsByVersion
      priority = Priority(version, displayVersions)
      loc <- locs
    } yield SitemapUrl(loc, priority)

    Sitemap(sitemapUrls)
  }

}
