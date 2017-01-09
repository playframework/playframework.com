package controllers

import javax.inject.Inject

import play.api.mvc.{AbstractController, ControllerComponents}
import services.github.ContributorsSummariser

import scala.concurrent.ExecutionContext

class Code @Inject() (
  contributorsSummariser: ContributorsSummariser,
  components: ControllerComponents)(implicit executionContext: ExecutionContext) extends AbstractController(components) {

  def index = Action.async { implicit req =>
    contributorsSummariser.fetchContributors.map { contributors =>
      Ok(views.html.code(contributors))
    }
  }
}