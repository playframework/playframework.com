package models.documentation

import play.api.i18n.Lang

/**
 * A translation context.
 *
 * A translation context exists for a particular version of the documentation in a particular translation, and couples
 * the alternative translations for that particular version with it.
 *
 * The alternative translations may be for the exact same version, the same major version, or for no version. The best
 * fit will be selected - if no version, it means no alternative is available for this major version in that
 * translation.
 *
 * @param lang The language of this translation
 * @param isDefault Whether this is the default translation
 * @param version The version of this context (if there is one)
 * @param alternatives Alternative translations of this documentation
 */
case class TranslationContext(lang: Lang, isDefault: Boolean, version: Option[Version], displayVersions: List[Version],
                              alternatives: List[AlternateTranslation]) {
  /**
   * The language as an alternative language, returning none if it's the default language.
   */
  val alternateLang = if (isDefault) None else Some(lang)
}

/**
 * An alternative translation.
 *
 * @param lang The language that this translation is for
 * @param isDefault Whether this is the default translation
 * @param version The version of the documentation that exists for this translation
 */
case class AlternateTranslation(lang: Lang, isDefault: Boolean, version: Option[Version])
