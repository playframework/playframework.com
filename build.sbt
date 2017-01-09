import JsEngineKeys._

JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

name := "playframework"
version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, NewRelic)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-doc" % "1.7.0",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "3.0.0.201306101825-r",
  "mysql" % "mysql-connector-java" % "5.1.18", // TODO: 5.1.34
  "com.damnhandy" % "handy-uri-templates" % "2.0.2",
  "org.webjars" % "jquery" % "1.8.2",
  "org.webjars" % "html5shiv" % "3.7.2",
  "org.webjars" % "prettify" % "4-Mar-2013",
  "org.webjars" % "clipboard.js" % "1.5.5",
  "com.typesafe.play" %% "anorm" % "2.6.0-SNAPSHOT",
  guice,
  jdbc,
  cache,
  evolutions,
  filters,
  ws,
  specs2 % "test"
)

scalaVersion := "2.12.4"

routesGenerator := InjectedRoutesGenerator

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
sources in (Compile, doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false

// NewRelic settings
newrelicVersion := "4.1.0"
newrelicAppName := "playframework.com"
