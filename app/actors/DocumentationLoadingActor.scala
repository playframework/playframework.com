package actors

import akka.actor.Actor
import akka.stream.scaladsl.{StreamConverters, Source}
import akka.util.ByteString
import org.apache.commons.io.IOUtils
import play.doc.{PlayDoc, FileRepository}
import utils._

object DocumentationLoadingActor {
  case class RenderPage(page: String, repo: PlayDoc)
  case class RenderV1Page(page: String, repo: ExtendedFileRepository)
  case class RenderV1Cheatsheet(category: String, repo: ExtendedFileRepository)
  case class V1Cheatsheet(sheets: Seq[String], title: String, otherCategories: Map[String, String])
  case class LoadResource(file: String, repo: FileRepository)
  case class Resource(content: Source[ByteString, _], size: Long)
  case class PageExists(page: String, playDoc: PlayDoc, repo: FileRepository)
  case class V1PageExists(page: String, repo: FileRepository)
}

/**
 * The documentation loading actor is responsible for loading and rendering documentation pages.
 *
 * All the operations on this actor are intended to be short - ie, load and/or render a single resource.
 *
 * In some cases this actor will return enumerators of content, the content is backed by blocking IO streams, but
 * the enumerator uses this actors own dispatcher to do that blocking IO.
 *
 * This actor is designed to be used behind a router, configured with a dedicated thread pool for doing blocking IO
 * operations.
 */
class DocumentationLoadingActor extends Actor {
  import DocumentationLoadingActor._

  def receive = {
    case RenderPage(page, playDoc) =>
      sender() ! playDoc.renderPage(page)

    case LoadResource(file, repo) =>
      val resource = repo.handleFile(file) { handle =>
        Resource(StreamConverters.fromInputStream(() => handle.is), handle.size)
      }
      sender() ! resource

    case RenderV1Page(page, repo) =>
      val content = repo.loadFile(s"manual/$page.textile")(IOUtils.toString(_, "utf-8"))
      val html = content.map(Textile.toHTML)
      sender() ! html

    case RenderV1Cheatsheet(category, repo) =>
      import scala.collection.JavaConverters._

      val sheetFiles = repo.listAllFilesInPath(s"cheatsheets/$category")
      val sortedSheets = CheatSheetHelper.sortSheets(sheetFiles.filter(_.endsWith(".textile")).toArray)
      if (sortedSheets.nonEmpty) {
        val sheets = sortedSheets.flatMap { file =>
          repo.loadFile(s"cheatsheets/$category/$file")(is => Textile.toHTML(IOUtils.toString(is, "utf-8")))
        }
        val title = CheatSheetHelper.getCategoryTitle(category)
        val otherCategories = CheatSheetHelper.listCategoriesAndTitles(repo.listAllFilesInPath("cheatsheets")
          .map(_.takeWhile(_ != '/')).toSet.toArray).asScala.toMap

        sender() ! Some(V1Cheatsheet(sheets, title, otherCategories))
      } else {
        sender() ! None
      }

    case PageExists(page, playDoc, repo) =>
      sender() ! (playDoc.pageIndex match {
        case Some(index) =>
          index.get(page).isDefined
        case None =>
          repo.findFileWithName(s"$page.md").isDefined
      })

    case V1PageExists(page, repo) =>
      sender() ! repo.findFileWithName(s"$page.textile").isDefined

  }
}
