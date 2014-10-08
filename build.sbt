import JsEngineKeys._

JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

name := "playframework"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-doc" % "1.2.1",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "3.0.0.201306101825-r",
  "mysql" % "mysql-connector-java" % "5.1.18",
  anorm,
  jdbc,
  filters,
  cache,
  ws
)

scalaVersion := "2.11.2"

StylusKeys.useNib in Assets := true

StylusKeys.compress in Assets := true

pipelineStages := Seq(digest, gzip)

sourceGenerators in Compile += Def.task {
  val siteVersionFile = crossTarget.value / "version" / "SiteVersion.scala"
  val gitHash = "git rev-parse HEAD".!!.trim
  if (!siteVersionFile.exists || !IO.read(siteVersionFile).contains(gitHash)) {
    IO.write(siteVersionFile,
      """package utils
        |
        |object SiteVersion {
        |  val hash = "%s"
        |}
      """.stripMargin.format(gitHash))
  }
  Seq(siteVersionFile)
}.taskValue

managedSourceDirectories in Compile += crossTarget.value / "version"
