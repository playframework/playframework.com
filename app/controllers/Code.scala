package controllers

import javax.inject.Inject
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import services.github.ContributorsSummariser

class Code @Inject() (contributorsSummariser: ContributorsSummariser) extends Controller {

  def index = Action.async { implicit req =>
    contributorsSummariser.fetchContributors.map { contributors =>
      Ok(views.html.code(contributors))
    }
  }
}