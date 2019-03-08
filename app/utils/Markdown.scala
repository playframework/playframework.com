package utils

import org.pegdown._
import org.pegdown.ast._

object Markdown {

  def toHtml(markdown: String, link: String => (String, String) = a => (a, a)): String = {
    val processor = new PegDownProcessor(Extensions.ALL)
    val links = new LinkRenderer {
      override def render(node: WikiLinkNode) = {
        val (href, text) = link(node.getText)
        new LinkRenderer.Rendering(href, text)
      }
    }
    processor.markdownToHtml(markdown, links)
  }

}
