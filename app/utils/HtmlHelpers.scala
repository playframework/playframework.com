package utils

object HtmlHelpers {

  // See https://stackoverflow.com/a/7594052/4600
  private val splitRegex = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"

  def friendlyTitle(page: String): String = page.split(splitRegex).mkString(" ").capitalize
}
