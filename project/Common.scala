import sbt._
import Keys._
import play.sbt.PlayImport._

object Common {
  val settings: Seq[Setting[_]] = Seq(
    scalaVersion := "2.11.8"
  )

  val libraryDependencies = Seq(
    "joda-time" % "joda-time" % "2.9.2",
    "org.joda" % "joda-convert" % "1.8.1",
    specs2 % "test"
  )
}