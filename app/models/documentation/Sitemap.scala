package models.documentation

import java.nio.file.Paths

import play.api.i18n.Lang
import play.doc.TocTree
import play.doc.Toc
import play.doc.TocPage

import scala.xml.Node

case class Priority(value: Double) {
  require(value <= Priority.Max, s"Sitemap priority cannot be greater than ${Priority.Max}")
  require(value >= Priority.Min, s"Sitemap priority cannot be less than ${Priority.Min}")
}

object Priority {
  val Max = 1.0
  val Min = 0.1 // Note: Google's docs say 0.1, but sitemaps.org says 0.0

  /**
   * Docs for the latest Play version get a priority of 0.9,
   * i.e. high priority but not as high as important pages like the download page.
   *
   * Docs for older versions don't get added to the sitemap,
   * so they get indexed with the default priority of 0.5.
   */
  val LatestDocumentation = Priority(0.9)

}

case class SitemapUrl(loc: String, priority: Priority, alternates: Map[Lang, String])

case class Sitemap(urls: Seq[SitemapUrl]) {

  def toXml: Node = {
    <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"
            xmlns:xhtml="http://www.w3.org/1999/xhtml">
    {
      urls.map { url =>
        <url>
        <loc>{url.loc}</loc>
        <priority>{"%.2f".format(url.priority.value)}</priority>
        {
          url.alternates.map { case (lang, href) =>
            <xhtml:link
          rel="alternate"
          hreflang={lang.code}
          href={href}/>
          }
        }
      </url>
      }
    }
    </urlset>
  }

}

object Sitemap {

  /** Extractor for a markdown file for a Play 2 documentation page */
  private object Play2DocPage {
    def unapply(path: String): Option[String] = {
      Paths.get(path).getFileName.toString match {
        case sidebar if sidebar.startsWith("_")   => None
        case markdown if markdown.endsWith(".md") => Some(markdown.dropRight(3))
        case other                                => None
      }
    }
  }

  /**
   * Find all the documentation pages for a given (translation, version) combination.
   * @return The page names, e.g. "ScalaTemplates", "ScalaRouting", ...
   */
  private def findPages(tv: TranslationVersion): Set[String] = {
    def findPagesInToc(toc: TocTree): Seq[String] = toc match {
      case TocPage(page, title, _)    => Seq(page)
      case Toc(name, title, nodes, _) => nodes.flatMap(node => findPagesInToc(node._2))
    }

    // Docs for Play 2.4.0 or newer have a PageIndex with a handy ToC to find the pages for us
    val foundInToC =
      for (pageIndex <- tv.playDoc.pageIndex) yield findPagesInToc(pageIndex.toc)

    // If for some reason we don't have a PageIndex, we need to find the pages the hard way
    foundInToC.getOrElse {
      tv.repo.listAllFilesInPath("manual").collect { case Play2DocPage(page) =>
        page
      }
    }.toSet
  }

  /**
   * Find the pages that have been translated into each language for a given version
   * @return (language -> pages)
   */
  private def findPageTranslations(
      translations: Map[Lang, Translation],
      version: Version,
  ): Map[Lang, Set[String]] = {
    translations.view.mapValues { trans =>
      (for {
        tv <- trans.byVersion.get(version)
      } yield findPages(tv)).getOrElse(Set.empty[String])
    }.toMap
  }

  private case class VersionedPageList(
      version: Version,
      pages: Set[String],
      translatedPages: Map[Lang, Set[String]],
  )

  /**
   * Generate a sitemap for the given documentation.
   *
   * Only the pages for the default version are included in the sitemap, all with priority 0.9.
   *
   * Note: this will perform I/O because it needs to look for files in git repos.
   */
  def apply(documentation: Documentation): Sitemap = {
    val defaultVersion = documentation.default.defaultVersion.toSeq

    val versionedPageLists: Seq[VersionedPageList] = for {
      version <- defaultVersion
      tv      <- documentation.default.byVersion.get(version)
      pages           = findPages(tv)
      translatedPages = findPageTranslations(documentation.translations, version)
    } yield {
      VersionedPageList(version, pages, translatedPages)
    }

    val sitemapUrls = for {
      VersionedPageList(version, pages, pageTranslations) <- versionedPageLists
      page                                                <- pages
    } yield {
      val loc = s"https://www.playframework.com/documentation/$version/$page"
      // for all langs that have the page translated for this version, add an alternate URL to the sitemap
      val alternates = pageTranslations.collect {
        case (lang, ps) if ps.contains(page) =>
          lang -> s"https://www.playframework.com/documentation/${lang.code}/$version/$page"
      }
      SitemapUrl(loc, Priority.LatestDocumentation, alternates)
    }

    Sitemap(sitemapUrls)
  }

}
