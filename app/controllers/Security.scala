package controllers

import play.api.mvc.{Action, Controller}
import play.api.Play
import play.twirl.api.Html
import utils.Markdown
import org.apache.commons.io.IOUtils
import java.io.File

object Security extends Controller with Common {

  def vulnerability(name: String) = Action { implicit req =>
    val path = "public/markdown/vulnerabilities/" + name

    // protect against dot dots
    if (new File("/" + path).getCanonicalPath != "/" + path) {
      notFound
    } else {
      Play.maybeApplication.flatMap(app => Option(app.classloader.getResourceAsStream(path + ".md"))).map { is =>
        val content = IOUtils.toString(is)

        try {
          Ok(views.html.security("Play Framework Security Advisory", Html(Markdown.toHtml(content, link => (link, link)))))
            .withHeaders(CACHE_CONTROL -> "max-age=10000")
        } finally {
          is.close()
        }
      } getOrElse notFound
    }
  }

  def index = Action { implicit req =>
    Ok(views.html.vulnerabilities(req)).withHeaders(CACHE_CONTROL -> "max-age=1000")
  }

}
