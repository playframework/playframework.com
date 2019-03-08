package models.github

import play.api.libs.json._

case class Contributors(
    committers: Seq[GitHubUser],
    playOrganisation: Seq[GitHubUser],
    contributors: Seq[GitHubUser],
)

case class Team(id: Long, name: String, url: String, members_url: String) {
  def membersUrl = members_url
}

object Team {
  implicit val jsonReads = Json.reads[Team]
}

/**
 * A GitHub user.
 *
 * The id, login, url, avatar_url and html_url fields are always available on a user object when referenced from
 * somewhere else.
 *
 * The name is only available when loading the user details from the user API, hence it is an option.
 */
case class GitHubUser(
    id: Long,
    login: String,
    url: String,
    avatar_url: String,
    html_url: String,
    name: Option[String],
) {
  def avatarUrl = avatar_url
  def htmlUrl   = html_url
}

object GitHubUser {
  implicit val jsonReads = Json.reads[GitHubUser]
}

case class Repository(id: Long, name: String, full_name: String, fork: Boolean, contributors_url: String) {
  def fullName        = full_name
  def contributorsUrl = contributors_url
}

object Repository {
  implicit val jsonReads = Json.reads[Repository]
}

case class Organisation(id: Long, login: String, url: String, repos_url: String, members_url: String) {
  def reposUrl   = repos_url
  def membersUrl = members_url
}

object Organisation {
  implicit val jsonReads = Json.reads[Organisation]
}
