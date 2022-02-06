package utils

import play.doc.FileHandle
import java.io.InputStream
import java.io.File
import org.eclipse.jgit.api.Git
import scala.jdk.CollectionConverters._
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter
import org.eclipse.jgit.treewalk.filter.TreeFilter
import org.eclipse.jgit.treewalk.filter.PathFilter
import org.eclipse.jgit.lib.FileMode
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.RepositoryBuilder
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.api.ListBranchCommand.ListMode
import org.eclipse.jgit.transport.TagOpt

class PlayGitRepository(val gitDir: File, val remote: String = "origin", basePath: Option[String] = None) {
  private val repository = new RepositoryBuilder().setGitDir(new File(gitDir, ".git")).build()
  private val git        = new Git(repository)

  def fileRepoForHash(hash: ObjectId): ExtendedFileRepository = {
    new GitFileRepository(this, hash, basePath)
  }

  def close(): Unit = repository.close()
  def allTags: Seq[(String, ObjectId)] =
    git.tagList().call().asScala.map(tag => tag.getName.stripPrefix("refs/tags/") -> tag.getLeaf.getObjectId).toSeq
  def allBranches: Seq[(String, ObjectId)] =
    git.branchList().setListMode(ListMode.REMOTE).call().asScala.collect {
      case origin if origin.getName.startsWith("refs/remotes/" + remote + "/") =>
        origin.getName.stripPrefix("refs/remotes/" + remote + "/") -> origin.getLeaf.getObjectId
    }.toSeq

  def hashForRef(ref: String): Option[ObjectId] =
    Option(repository.exactRef("refs/remotes/" + remote + "/" + ref))
      .map(_.getLeaf.getObjectId)

  def fetch(): Unit = {
    // Perform two fetches. The first fetch will remove deleted refs, the second fetch
    // doesn't remove anything. We need to run the second fetch because the JGit code
    // for removing deleted remote refs is buggy so the first fetch will remove the
    // remote main branch if it's already present. The second fetch will restore it.
    // The second fetch should be pretty lightweight to run again because there shouldn't
    // be much data that needs pulling down.
    git.fetch().setTagOpt(TagOpt.FETCH_TAGS).setRemoveDeletedRefs(true).setRemote(remote).call()
    git.fetch().setTagOpt(TagOpt.FETCH_TAGS).setRemote(remote).call()
  }

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
    private val pathRaw = Constants.encode(basePath)
    private val nameRaw = Constants.encode("/" + name)

    def shouldBeRecursive() = false

    def include(walker: TreeWalk): Boolean = {
      // The way include works is if it's a subtree (directory), then we return true if we want to descend into it,
      // and if it's not, then we return true if the file is the one we want.
      walker.isPathPrefix(pathRaw, pathRaw.length) == 0 &&
      (walker.isSubtree || walker.isPathSuffix(nameRaw, nameRaw.length))
    }

    override def clone(): FileWithNameFilter = this
  }

  def findFileWithName(hash: ObjectId, basePath: Option[String], name: String): Option[String] = {
    scanFiles(
      hash,
      basePath.map(new FileWithNameFilter(_, name)).getOrElse(PathSuffixFilter.create("/" + name)),
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
    val tree    = revWalk.parseCommit(hash).getTree

    // And walk it
    val treeWalk = new TreeWalk(repository)
    try {
      treeWalk.addTree(tree)
      treeWalk.setRecursive(true)
      treeWalk.setFilter(filter)
      block(treeWalk)
    } finally {
      // Clean up
      treeWalk.close()
      revWalk.dispose()
    }
  }
}

class GitFileRepository(playRepo: PlayGitRepository, hash: ObjectId, base: Option[String])
    extends ExtendedFileRepository {

  def resolve(path: String): String = {
    // Split into parts, fold it from the right back into parts, skipping any part after a .. part
    val resolvedPath = path
      .split("/")
      .foldRight((List.empty[String], 0)) {
        case (part, (parts, toSkip)) if part == ".." => (parts, toSkip + 1)
        case (part, (parts, toSkip)) if toSkip > 0   => (parts, toSkip - 1)
        case (part, (parts, _))                      => (part :: parts, 0)
      }
      ._1
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
      case (length, is) =>
        handler(FileHandle(path.drop(path.lastIndexOf('/') + 1), length, is, () => is.close()))
    }
  }

  def listAllFilesInPath(path: String) = playRepo.listAllFilesInPath(hash, resolve(path))

  override def toString = {
    s"GitFileRepository(${playRepo.gitDir}:${playRepo.remote}@${hash.name}})"
  }
}
