package utils

import play.doc.{FileHandle, FileRepository}
import java.io.{InputStream, File}
import org.eclipse.jgit.api.Git
import scala.collection.JavaConversions._
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.{PathSuffixFilter, TreeFilter, PathFilter}
import org.eclipse.jgit.lib.{FileMode, ObjectId, RepositoryBuilder, Constants}
import org.eclipse.jgit.api.ListBranchCommand.ListMode
import org.eclipse.jgit.transport.TagOpt

class PlayGitRepository(val gitDir: File, val remote: String = "origin", basePath: Option[String] = None) {
  private val repository = new RepositoryBuilder().setGitDir(new File(gitDir, ".git")).build()
  private val git = new Git(repository)

  def fileRepoForHash(hash: ObjectId): ExtendedFileRepository = {
    new GitFileRepository(this, hash, basePath)
  }

  def close() = repository.close()
  def allTags: Seq[(String, ObjectId)] = git.tagList().call().map(tag =>
    tag.getName.stripPrefix("refs/tags/") -> tag.getLeaf.getObjectId
  )
  def allBranches: Seq[(String, ObjectId)] = git.branchList().setListMode(ListMode.REMOTE).call().collect {
    case origin if origin.getName.startsWith("refs/remotes/" + remote + "/") =>
      origin.getName.stripPrefix("refs/remotes/" + remote + "/") -> origin.getLeaf.getObjectId
  }

  def hashForRef(ref: String): Option[ObjectId] =
    Option(repository.getRef("refs/remotes/" + remote + "/" + ref))
      .map(_.getLeaf.getObjectId)

  def fetch(): Unit = git.fetch().setTagOpt(TagOpt.FETCH_TAGS)
    .setRemoveDeletedRefs(true).setRemote(remote).call()

  private def treeWalkIsFile(walk: TreeWalk) = {
    (walk.getFileMode(0).getBits & FileMode.TYPE_MASK) == FileMode.TYPE_FILE
  }

  /**
   * Load the file with the given path in the given ref
   *
   * @param hash The hash of the commit to load from
   * @param path The path to load
   * @return A tuple of the file size and its input stream, if the file was found
   */
  def loadFile(hash: ObjectId, path: String): Option[(Long, InputStream)] = {
    scanFiles(hash, PathFilter.create(path)) { treeWalk =>
      if (!treeWalk.next()) {
        None
      } else if (treeWalkIsFile(treeWalk)) {
        val file = repository.open(treeWalk.getObjectId(0))
        Some((file.getSize, file.openStream()))
      } else {
        None
      }
    }
  }

  // A tree filter that finds files with the given name under the given base path
  private class FileWithNameFilter(basePath: String, name: String) extends TreeFilter {
    val pathRaw = Constants.encode(basePath)

    // Due to this bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=411999
    // we have to supply a byte array that has one dummy byte at the front of it.
    // As soon as that bug is fixed, this code will break, just remove the #.
    val nameRaw = Constants.encode("#/" + name)

    def shouldBeRecursive() = false

    def include(walker: TreeWalk) = {
      // The way include works is if it's a subtree (directory), then we return true if we want to descend into it,
      // and if it's not, then we return true if the file is the one we want.
      walker.isPathPrefix(pathRaw, pathRaw.length) == 0 &&
        (walker.isSubtree || walker.isPathSuffix(nameRaw, nameRaw.length))
    }

    override def clone() = this
  }

  def findFileWithName(hash: ObjectId, basePath: Option[String], name: String): Option[String] = {
    scanFiles(hash,
      basePath.map(new FileWithNameFilter(_, name)).getOrElse(PathSuffixFilter.create("#/" + name))
    ) { treeWalk =>
      if (!treeWalk.next()) {
        None
      } else if (treeWalkIsFile(treeWalk)) {
        Some(treeWalk.getPathString.drop(basePath.map(_.length + 1).getOrElse(0)))
      } else {
        None
      }
    }
  }

  def listAllFilesInPath(hash: ObjectId, path: String): Seq[String] = {
    scanFiles(hash, PathFilter.create(path)) { treeWalk =>
      def extract(list: List[String]): List[String] = {
        if (!treeWalk.next()) {
          list
        } else if (treeWalkIsFile(treeWalk)) {
          extract(treeWalk.getPathString.drop(path.length + 1) :: list)
        } else {
          extract(list)
        }
      }
      extract(Nil)
    }
  }

  private def scanFiles[T](hash: ObjectId, filter: TreeFilter)(block: TreeWalk => T): T = {
    // Now find the tree for that commit id
    val revWalk = new RevWalk(repository)
    val tree = revWalk.parseCommit(hash).getTree

    // And walk it
    val treeWalk = new TreeWalk(repository)
    try {
      treeWalk.addTree(tree)
      treeWalk.setRecursive(true)
      treeWalk.setFilter(filter)
      block(treeWalk)
    } finally {
      // Clean up
      treeWalk.release()
      revWalk.dispose()
    }
  }
}

class GitFileRepository(playRepo: PlayGitRepository, hash: ObjectId, base: Option[String]) extends ExtendedFileRepository {

  def resolve(path: String): String = {
    // Split into parts, fold it from the right back into parts, skipping any part after a .. part
    val resolvedPath = path.split("/").foldRight((List.empty[String], 0)) {
      case (part, (parts, toSkip)) if part == ".." => (parts, toSkip + 1)
      case (part, (parts, toSkip)) if toSkip > 0 => (parts, toSkip - 1)
      case (part, (parts, _)) => (part :: parts, 0)
    }._1
    // Prepend the base if it exists, and make back into a string
    base.fold(resolvedPath)(_ :: resolvedPath).mkString("/")
  }

  def loadFile[A](path: String)(loader: (InputStream) => A) = {
    playRepo.loadFile(hash, resolve(path)).map { file =>
      try {
        loader(file._2)
      } finally {
        file._2.close()
      }
    }
  }

  def findFileWithName(name: String) = playRepo.findFileWithName(hash, base, name)

  def handleFile[A](path: String)(handler: (FileHandle) => A) = {
    playRepo.loadFile(hash, resolve(path)).map {
      case (length, is) => handler(FileHandle(path.drop(path.lastIndexOf('/') + 1), length, is, () => is.close()))
    }
  }

  def listAllFilesInPath(path: String) = playRepo.listAllFilesInPath(hash, resolve(path))

  override def toString = {
    s"GitFileRepository(${playRepo.gitDir}:${playRepo.remote}@${hash.name}})"
  }
}
