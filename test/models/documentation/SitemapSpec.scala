package models.documentation

import org.specs2.mutable.Specification

class SitemapSpec extends Specification {

  "priority calculation" should {
    "favour recent versions and stable versions" in {
      val displayVersions = Seq(
        "3.0.x",
        "2.4.x",
        "2.4.2",
        "2.4.1",
        "2.4.0",
        "2.3.x",
        "2.2.x",
        "2.1.x",
        "1.3.x",
        "1.1.1"
      ).map(Version.parse(_).get)

      def checkPriority(version: String, expectedPriority: Double) = {
        val priority = Priority(Version.parse(version).get, displayVersions)
        priority.value must beCloseTo(expectedPriority, 0.01)
      }

      checkPriority("3.0.x", 0.8)
      checkPriority("2.4.x", 0.7)
      checkPriority("2.4.2", 0.8)
      checkPriority("2.4.1", 0.7)
      checkPriority("2.4.0", 0.6)
      checkPriority("2.3.x", 0.3)
      checkPriority("2.2.x", 0.2)
      checkPriority("1.3.x", 0.1)
      checkPriority("1.1.1", 0.1)
    }
  }
}
