import JsEngineKeys._

JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

name := "playframework"
version := "1.0-SNAPSHOT"

lazy val utils = (project in file("modules/utils"))

lazy val certificationApi = (project in file("modules/certification/api"))

lazy val certificationDb = (project in file("modules/certification/db"))
  .dependsOn(certificationApi)
  .aggregate(certificationApi)
  .settings(
    libraryDependencies ++= Seq(
      jdbc,
      "com.typesafe.play" %% "anorm" % "2.4.0"
    )
  )

lazy val moduleApi = (project in file("modules/module/api"))

lazy val moduleDb = (project in file("modules/module/db"))
  .dependsOn(moduleApi, utils)
  .aggregate(moduleApi, utils)
  .settings(
    libraryDependencies ++= Seq(
      jdbc,
      "com.typesafe.play" %% "anorm" % "2.4.0"
    )
  )

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .dependsOn(utils, certificationApi, moduleApi, certificationDb, moduleDb)
  .aggregate(utils, certificationApi, moduleApi, certificationDb, moduleDb)

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.18", // TODO: 5.1.34
  "com.damnhandy" % "handy-uri-templates" % "2.0.2",
  "org.webjars" % "jquery" % "1.8.2",
  "org.webjars" % "html5shiv" % "3.7.2",
  "org.webjars" % "prettify" % "4-Mar-2013",
  "org.webjars" % "clipboard.js" % "1.5.5",
  evolutions,
  filters,
  ws,
  specs2 % "test"
)

Common.settings

libraryDependencies ++= Common.libraryDependencies

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

import ByteConversions._

BundleKeys.nrOfCpus := 1.0
BundleKeys.memory := 1.GiB
BundleKeys.diskSpace := 4.GiB
BundleKeys.roles := Set("users.human")

BundleKeys.endpoints := Map(
  "web" -> Endpoint("http", services = Set(URI("http://www.playframework.com"), URI("http://playframework.com")))
)

inConfig(Bundle)(Seq(
  bintrayVcsUrl := Some("https://github.com/playframework/playframework.com"),
  bintrayOrganization := Some("typesafe"),
  bintrayRepository := "internal-bundle",
  bintrayReleaseOnPublish := true
))
