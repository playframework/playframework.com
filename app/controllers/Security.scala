package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.{AbstractController, Action, Controller, ControllerComponents}
import play.api.Environment
import play.twirl.api.Html
import utils.Markdown
import org.apache.commons.io.IOUtils
import java.io.File

@Singleton
class Security @Inject() (environment: Environment, components: ControllerComponents) extends AbstractController(components) with Common {

  def vulnerability(name: String) = Action { implicit req =>
    val path = "public/markdown/vulnerabilities/" + name

    // protect against dot dots
    if (new File("/" + path).getCanonicalPath != "/" + path) {
      notFound
    } else {
      environment.resourceAsStream(path + ".md").map { is =>
        val content = IOUtils.toString(is, "utf-8")

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
