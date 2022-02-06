package utils

import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.InitCommand
import org.eclipse.jgit.lib.PersonIdent
import org.specs2.mutable.Specification
import java.io.File
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils

class GitFileRepositorySpec extends Specification {

  def withPlayRepo[T](block: PlayGitRepository => T): T = {
    // Create a temporary directory
    val tmpdir = File.createTempFile("gitrepo", "")
    tmpdir.delete()
    tmpdir.mkdir()

    try {
      val repodir = new File(tmpdir, "repo")

      val encoding = "UTF-8"

      // Some files in it
      FileUtils.write(
        new File(repodir, "documentation/manual/working/scalaGuide/ScalaHome.md"),
        "Hello!",
        encoding,
      )
      FileUtils.write(
        new File(repodir, "documentation/manual/working/scalaGuide/main/http/code/ScalaActions.scala"),
        "Action",
        encoding,
      )
      FileUtils.write(new File(repodir, "framework/build.sbt"), "Build", encoding)

      // Turn it into a git repo
      val repo = new InitCommand().setDirectory(repodir).call()

      val author = new PersonIdent("Unit Test", "unit.test@example.org")

      // Add the files and commit
      repo.add().addFilepattern(".").call()
      repo.commit().setAuthor(author).setMessage("Initial commit").call()

      // Create some branches
      repo.branchCreate().setName("2.1.x").call()
      repo.branchCreate().setName("2.0.x").call()
      // Create some tags
      repo.tag().setTagger(author).setAnnotated(true).setName("2.1.0").setMessage("2.1.0").call()
      repo.tag().setTagger(author).setAnnotated(true).setName("2.1.1").setMessage("2.1.1").call()

      // Now create another git repo, and set this one to be a remote, since Play only works with remote refs
      val clonedir = new File(tmpdir, "clone")
      val clone    = new CloneCommand().setDirectory(clonedir).setURI(repodir.toURI.toString).call()

      val playRepo = new PlayGitRepository(clonedir)

      block(playRepo)
    } finally {
      FileUtils.deleteDirectory(tmpdir)
    }
  }

  def withGitRepo[T](block: GitFileRepository => T): T = withPlayRepo { playRepo =>
    val gitRepo =
      new GitFileRepository(playRepo, playRepo.hashForRef("main").orNull, Some("documentation/manual"))
    block(gitRepo)
  }

  "git file repository" should {

    "find a file" in withGitRepo { gitRepo =>
      gitRepo.findFileWithName("ScalaHome.md") must beSome("working/scalaGuide/ScalaHome.md")
    }

    "load a file" in withGitRepo { gitRepo =>
      gitRepo.loadFile("working/scalaGuide/ScalaHome.md")(IOUtils.toString(_, "utf-8")) must beSome("Hello!")
    }

    "find all tags" in withPlayRepo { playRepo =>
      playRepo.allTags.map(_._1) must containAllOf(Seq("2.1.0", "2.1.1"))
    }

    "find all branches" in withPlayRepo { playRepo =>
      playRepo.allBranches.map(_._1) must containAllOf(Seq("2.1.x", "2.0.x"))
    }

    "work with relative paths" in withGitRepo { gitRepo =>
      gitRepo.loadFile("working/scalaGuide/main/async/../http/code/ScalaActions.scala")(
        IOUtils.toString(_, "utf-8"),
      ) must beSome("Action")
    }

    "work with doubly relative paths" in withGitRepo { gitRepo =>
      gitRepo.loadFile("working/scalaGuide/main/async/code/../../http/code/ScalaActions.scala")(
        IOUtils.toString(_, "utf-8"),
      ) must beSome("Action")
    }

    "not escape outside the base path" in withGitRepo { gitRepo =>
      gitRepo.loadFile("../../framework/build.sbt")(IOUtils.toString(_, "utf-8")) must beNone
    }

  }

}
