name    := "playframework"
version := "1.0-SNAPSHOT"

enablePlugins(PlayScala)

scalaVersion := "3.3.7"
scalacOptions ++= List("-encoding", "utf8", "-deprecation", "-feature", "-unchecked")

libraryDependencies ++= Seq(
  "org.playframework" %% "play-doc"            % "3.0.1",
  "org.eclipse.jgit"   % "org.eclipse.jgit"    % "7.4.0.202509020913-r",
  "com.damnhandy"      % "handy-uri-templates" % "2.1.8",
  "org.webjars"        % "jquery"              % "3.7.1",
  "org.webjars"        % "html5shiv"           % "3.7.3-1",
  "org.webjars"        % "prettify"            % "4-Mar-2013-1",
  "org.webjars"        % "clipboard.js"        % "2.0.11",
  guice,
  ehcache,
  filters,
  ws,
  specs2 % Test,
)

routesGenerator := InjectedRoutesGenerator

JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

Assets / StylusKeys.useNib   := true
Assets / StylusKeys.compress := true

pipelineStages := Seq(digest, gzip)

Compile / sourceGenerators += Def.task {
  import scala.sys.process._
  val siteVersionFile = crossTarget.value / "version" / "SiteVersion.scala"
  val gitHash         = "git rev-parse HEAD".!!.trim
  if (!siteVersionFile.exists || !IO.read(siteVersionFile).contains(gitHash)) {
    IO.write(
      siteVersionFile,
      """package utils
        |
        |object SiteVersion {
        |  val hash = "%s"
        |}
      """.stripMargin.format(gitHash),
    )
  }
  Seq(siteVersionFile)
}.taskValue

Compile / managedSourceDirectories += crossTarget.value / "version"
Compile / doc / sources                := Seq.empty
Compile / packageDoc / publishArtifact := false
