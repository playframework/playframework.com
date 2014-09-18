package utils

import org.specs2.mutable.Specification
import java.io.File
import org.apache.commons.io.IOUtils

class GitFileRepositorySpec extends Specification {

  "git file repository" should {
    val playRepo = new PlayGitRepository(new File("data/main"))
    val gitRepo = new GitFileRepository(playRepo, playRepo.hashForRef("master").orNull, Some("documentation/manual"))

    "find a file" in {
      gitRepo.findFileWithName("ScalaHome.md") must beSome("working/scalaGuide/ScalaHome.md")
    }

    "load a file" in {
      gitRepo.loadFile("working/scalaGuide/main/http/ScalaRouting.md")(IOUtils.toString) must beSome.like {
        case s => s must contain("HTTP")
      }
    }

    "find all tags" in {
      playRepo.allTags.map(_._1) must containAllOf(Seq("2.1.0", "2.1.1"))
    }

    "find all branches" in {
      playRepo.allBranches.map(_._1) must containAllOf(Seq("2.1.x", "2.0.x"))
    }

  }

}