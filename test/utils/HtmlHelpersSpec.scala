package utils

import org.specs2.mutable.Specification

class HtmlHelpersSpec extends Specification {

  "HtmlHelpers" should {
    "when rendering title" in {
      "split the camel case title" in {
        val input = "ScalaTemplates"
        HtmlHelpers.friendlyTitle(input) must_== "Scala Templates"
      }

      "split the camel case title when there is an acronym" in {
        "at the end of the page name" in {
          val input = "JavaJPA"
          HtmlHelpers.friendlyTitle(input) must_== "Java JPA"
        }

        "at the begging of the page name" in {
          val input = "CSPFilter"
          HtmlHelpers.friendlyTitle(input) must_== "CSP Filter"
        }

        "in the middle of the page name" in {
          val input = "ScalaSIRDRouter"
          HtmlHelpers.friendlyTitle(input) must_== "Scala SIRD Router"
        }
      }

      "capitalize title's first word" in {
        val input = "somePageTitle"
        HtmlHelpers.friendlyTitle(input) must_== "Some Page Title"
      }

      "do not split when it is a single work" in {
        val input = "Scala"
        HtmlHelpers.friendlyTitle(input) must_== "Scala"
      }
    }
  }
}
