package controllers

import javax.inject.Inject

import play.api.mvc.AbstractController
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
import services.github.ContributorsSummariser

import scala.concurrent.ExecutionContext

class Code @Inject() (contributorsSummariser: ContributorsSummariser, components: ControllerComponents)(using
    executionContext: ExecutionContext,
    reverseRouter: documentation.ReverseRouter,
) extends AbstractController(components) {

  def index = Action.async { case given Request[AnyContent] =>
    contributorsSummariser.fetchContributors.map { contributors =>
      Ok(views.html.code(contributors))
    }
  }
}
