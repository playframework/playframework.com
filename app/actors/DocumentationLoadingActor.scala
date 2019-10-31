package actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.scaladsl.StreamConverters
import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.apache.commons.io.IOUtils
import play.doc.PlayDoc
import play.doc.FileRepository
import utils._

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
object DocumentationLoadingActor {
  sealed trait Command
  case class RenderPage(page: String, repo: PlayDoc, replyTo: ActorRef[Option[play.doc.RenderedPage]]) extends Command
  case class RenderV1Page(page: String, repo: ExtendedFileRepository, replyTo: ActorRef[Option[String]]) extends Command
  case class RenderV1Cheatsheet(category: String, repo: ExtendedFileRepository, replyTo: ActorRef[Option[V1Cheatsheet]]) extends Command
  case class LoadResource(file: String, repo: FileRepository, replyTo: ActorRef[Option[Resource]]) extends Command
  case class PageExists(page: String, playDoc: PlayDoc, repo: FileRepository, replyTo: ActorRef[Boolean]) extends Command
  case class V1PageExists(page: String, repo: FileRepository, replyTo: ActorRef[Boolean]) extends Command

  case class V1Cheatsheet(sheets: Seq[String], title: String, otherCategories: Map[String, String])
  case class Resource(content: Source[ByteString, _], size: Long)

  def apply(): Behavior[Command] = Behaviors.receiveMessage {
    case RenderPage(page, playDoc, replyTo) =>
      replyTo ! playDoc.renderPage(page)
      Behaviors.same

    case LoadResource(file, repo, replyTo) =>
      val resource = repo.handleFile(file) { handle =>
        Resource(StreamConverters.fromInputStream(() => handle.is), handle.size)
      }
      replyTo ! resource
      Behaviors.same

    case RenderV1Page(page, repo, replyTo) =>
      val content = repo.loadFile(s"manual/$page.textile")(IOUtils.toString(_, "utf-8"))
      val html    = content.map(Textile.toHTML)
      replyTo ! html
      Behaviors.same

    case RenderV1Cheatsheet(category, repo, replyTo) =>
      import scala.collection.JavaConverters._

      val sheetFiles   = repo.listAllFilesInPath(s"cheatsheets/$category")
      val sortedSheets = CheatSheetHelper.sortSheets(sheetFiles.filter(_.endsWith(".textile")).toArray)
      if (sortedSheets.nonEmpty) {
        val sheets = sortedSheets.flatMap { file =>
          repo.loadFile(s"cheatsheets/$category/$file")(is => Textile.toHTML(IOUtils.toString(is, "utf-8")))
        }
        val title = CheatSheetHelper.getCategoryTitle(category)
        val otherCategories = CheatSheetHelper
          .listCategoriesAndTitles(
            repo
              .listAllFilesInPath("cheatsheets")
              .map(_.takeWhile(_ != '/'))
              .toSet
              .toArray,
          )
          .asScala
          .toMap

        replyTo ! Some(V1Cheatsheet(sheets, title, otherCategories))
      } else {
        replyTo ! None
      }
      Behaviors.same

    case PageExists(page, playDoc, repo, replyTo) =>
      replyTo ! (playDoc.pageIndex match {
        case Some(index) =>
          index.get(page).isDefined
        case None =>
          repo.findFileWithName(s"$page.md").isDefined
      })
      Behaviors.same

    case V1PageExists(page, repo, replyTo) =>
      replyTo ! repo.findFileWithName(s"$page.textile").isDefined
      Behaviors.same
  }
}
