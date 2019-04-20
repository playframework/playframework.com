package models.documentation

import java.io.File

import play.api.i18n.Lang

import scala.util.matching.Regex

case class DocumentationConfig(default: TranslationConfig, translations: Seq[TranslationConfig])

case class RedirectPage(from: String, to: String)

case class DocumentationRedirects(redirects: Seq[RedirectPage]) {
  def redirectFor(page: String): Option[RedirectPage] = redirects.find(_.from == page)
}

case class TranslationConfig(
    lang: Lang,
    repo: File,
    basePath: Option[String],
    remote: String,
    masterVersion: Option[MasterVersion],
    gitHubSource: Option[String],
)

case class MasterVersion(file: String, pattern: Regex)
