package models.documentation

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
