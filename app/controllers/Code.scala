package controllers

import javax.inject.Inject
import play.api.mvc._
import services.ContributorsSummariser
import play.api.libs.concurrent.Execution.Implicits._

class Code @Inject() (contributorsSummariser: ContributorsSummariser) extends Controller {

  def index = Action.async { implicit req =>
    contributorsSummariser.fetchContributors.map { contributors =>
      Ok(views.html.code(contributors))
    }
  }
}