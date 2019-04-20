package models.documentation

import org.specs2.mutable.Specification

class DocumentationSpec extends Specification {

  "Version.parse" should {
    "parse a major version" in {
      (Version.parse("1.2") must be).some(Version("1.2", 1, 2, 0, 0, Release))
    }
    "parse a major minor version" in {
      (Version.parse("1.2.3") must be).some(Version("1.2.3", 1, 2, 3, 0, Release))
    }
    "parse a release candidate version" in {
      (Version.parse("1.2-RC2") must be).some(Version("1.2-RC2", 1, 2, 0, 0, ReleaseCandidate(2)))
    }
    "parse a milestone version" in {
      (Version.parse("1.2-M4") must be).some(Version("1.2-M4", 1, 2, 0, 0, Milestone(4)))
    }
    "parse a snapshot version" in {
      (Version.parse("1.2.3-SNAPSHOT") must be).some(Version("1.2.3-SNAPSHOT", 1, 2, 3, 0, Latest))
    }
    "parse a wildcard version" in {
      (Version.parse("1.2.x") must be).some(Version("1.2.x", 1, 2, 9999, 0, Latest))
    }
    "ignore versions that aren't numbers" in {
      Version.parse("blah") must beNone
    }
    "ignore unknown version types" in {
      Version.parse("1.2-BLAH1") must beNone
    }
    "parse a patch version" in {
      (Version.parse("1.2.3.4") must be).some(Version("1.2.3.4", 1, 2, 3, 4, Release))
    }
  }

  "Version sorting" should {
    "sort first by era" in {
      Version.parse("1.4.4.4") must beLessThan(Version.parse("2.1.1.1-SNAPSHOT"))
    }
    "sort second by major number" in {
      Version.parse("1.1.4.4") must beLessThan(Version.parse("1.2.1.1-SNAPSHOT"))
    }
    "sort third by minor number" in {
      Version.parse("1.1.1.4") must beLessThan(Version.parse("1.1.2.1-SNAPSHOT"))
    }
    "sort third by patch number" in {
      Version.parse("1.1.1.1") must beLessThan(Version.parse("1.1.1.2-SNAPSHOT"))
    }
    "consider snapshot the greatest version" in {
      Version.parse("2.0-SNAPSHOT") must beGreaterThan(Version.parse("2.0"))
      Version.parse("2.0-SNAPSHOT") must beGreaterThan(Version.parse("2.0-RC1"))
      Version.parse("2.0-SNAPSHOT") must beGreaterThan(Version.parse("2.0-M1"))
    }
    "consider release the second greatest version" in {
      Version.parse("2.0") must beGreaterThan(Version.parse("2.0-RC1"))
      Version.parse("2.0") must beGreaterThan(Version.parse("2.0-M1"))
    }
    "consider rc the third greatest version" in {
      Version.parse("2.0-RC1") must beGreaterThan(Version.parse("2.0-M1"))
    }
    "sort by extra number" in {
      Version.parse("2.1-RC2") must beGreaterThan(Version.parse("2.1-RC1"))
    }
  }

  def mockTranslation(versions: String*): Translation = {
    Translation(
      versions.toList.collect(Function.unlift(Version.parse)).sorted.reverse.map { version =>
        TranslationVersion(version, null, null, "", "")
      },
      null,
      None,
    )
  }

  "display versions" should {
    implicit def p(s: String): Version = Version.parse(s).get
    "work like this" in {

      mockTranslation(
        "2.2-SNAPSHOT",
        "1.2.2",
        "1.2.1",
        "1.2.4",
        "1.2.4.1",
        "1.2.3",
        "1.3.x",
        "2.0.5-SNASPHOT",
        "2.0.4",
        "2.1.3-RC2",
        "2.1",
        "2.1.1",
        "2.1.2",
        "2.1.3-RC1",
      ).displayVersions must containAllOf(
        Seq[Version](
          "2.2-SNAPSHOT",
          "2.1.3-RC2",
          "2.1.2",
          "2.1.1",
          "2.1",
          "2.0.4",
          "1.3.x",
          "1.2.4.1",
        ),
      ).inOrder
    }
    "should only show most recent snapshot and development version" in {
      mockTranslation(
        "2.2-SNAPSHOT",
        "2.2.0-RC1",
        "2.2.0-M2",
        "2.1.4-RC1",
        "2.1.3",
      ).displayVersions must containAllOf(
        Seq[Version](
          "2.2-SNAPSHOT",
          "2.2.0-RC1",
          "2.1.3",
        ),
      ).inOrder
    }
  }
}
