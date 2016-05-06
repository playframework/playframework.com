package utils

import java.io.InputStream

import play.doc.{ FileHandle, FileRepository }

trait ExtendedFileRepository extends FileRepository {
  def listAllFilesInPath(path: String): Seq[String]
}

/**
 * A file repository that aggregates multiple file repositories
 *
 * @param repos The repositories to aggregate
 */
class AggregateFileRepository(repos: Seq[ExtendedFileRepository]) extends ExtendedFileRepository {

  def this(repos: Array[ExtendedFileRepository]) = this(repos.toSeq)

  private def fromFirstRepo[A](load: ExtendedFileRepository => Option[A]) = repos.collectFirst(Function.unlift(load))

  def loadFile[A](path: String)(loader: (InputStream) => A) = fromFirstRepo(_.loadFile(path)(loader))

  def handleFile[A](path: String)(handler: (FileHandle) => A) = fromFirstRepo(_.handleFile(path)(handler))

  def findFileWithName(name: String) = fromFirstRepo(_.findFileWithName(name))

  def listAllFilesInPath(path: String) = repos.flatMap(_.listAllFilesInPath(path)).distinct

  override def toString = {
    s"AggregateFileRepository(${repos.mkString(", ")}})"
  }
}
