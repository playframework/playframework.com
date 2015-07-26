package models.documentation

import scala.xml.Node

case class Priority(value: Double) {
  require(value <= 1.0, "Sitemap priority cannot be greater than 1.0")
  // Google's docs say >= 0.1, but sitemaps.org says >= 0.0
  require(value >= 0.0, "Sitemap priority cannot be less than 0.0")
}

case class SitemapUrl(loc: String, priority: Priority) // TODO links to translations

case class Sitemap(urls: Seq[SitemapUrl]) {

  def toXml: Node = {
    <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
    { urls map { url =>
      <url>
        <loc>{url.loc}</loc>
        <priority>{url.priority.value}</priority>
      </url>
    }}
    </urlset>
  }

}
