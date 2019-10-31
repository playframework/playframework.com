package utils

import java.io.File
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.Future
import scala.util.Random
import org.apache.commons.io.IOUtils
import scala.concurrent.duration.Duration
import java.util.concurrent.atomic.AtomicLong

object GitFileRepositoryPerformanceTest extends App {

  // Performance test should be performed on the actual Play git repository.
  // Substitute here with the path to your own git repository to run this test.
  val repo     = new PlayGitRepository(new File("data/main"))
  val basePath = "documentation/manual"

  // First, find all the files that we might want to look up
  val tags = repo.allTags
  val allFiles = tags.flatMap {
    case (_, ref) => repo.listAllFilesInPath(ref, basePath).map((ref, _))
  }
  // Filter markdown files
  val allMarkdown = allFiles.filter(_._2.endsWith(".md")).toIndexedSeq
  println("Testing with " + allMarkdown.size + " markdown files")

  def runTest(threads: Int, seconds: Long) = {
    @volatile var running      = true
    val findFileWithNameTiming = new AtomicLong()
    val loadFileTiming         = new AtomicLong()
    val tasks = Future.sequence(for (i <- 0 to threads) yield {
      Future {
        var markdownLoaded = 0

        while (running) {
          // Get a random markdown file
          val (ref, file) = allMarkdown(Random.nextInt(allMarkdown.size))
          // Strip the path off it, this is how requests come in
          val name = file.drop(file.lastIndexOf('/') + 1)
          // Find and then load it
          val fileRepo = new GitFileRepository(repo, ref, Some(basePath))

          def findFileWithName(name: String) = {
            val start  = System.nanoTime()
            val result = fileRepo.findFileWithName(name)
            findFileWithNameTiming.addAndGet(System.nanoTime() - start)
            result
          }

          def loadFile(path: String) = {
            val start  = System.nanoTime()
            val result = fileRepo.loadFile(path)(IOUtils.toString(_, "utf-8"))
            loadFileTiming.addAndGet(System.nanoTime() - start)
            result
          }

          markdownLoaded += findFileWithName(name).flatMap(loadFile).map(_ => 1).getOrElse(0)
        }
        markdownLoaded
      }
    })

    Thread.sleep(seconds * 1000)
    println("Stopping tests...")
    running = false
    val loaded = Await.result(tasks, Duration.Inf).reduce((a, b) => a + b)

    println("Loaded " + loaded + " files in " + seconds + " seconds")
    println("That's " + (loaded / seconds) + " files a second")
    println(s"Total time spent finding markdown files: ${(findFileWithNameTiming.get() / 1000000)}ms")
    println(s"Total time spent loading markdown files: ${(loadFileTiming.get() / 1000000)}ms")
  }

  println
  println("Test run 1:")
  println
  runTest(10, 10)

  println
  println("Test run 2:")
  println
  runTest(10, 10)

  println
  println("Test run 3:")
  println
  runTest(10, 10)
}
