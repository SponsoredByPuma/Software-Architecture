import dependencies._

lazy val allDependencies = Seq(
  guice,
  scalaxml,
  playjson,
  scalactic,
  scalatest,
  scalaswing,
  scalaguice,
  akkaHttp,
  akkaHttpSpray,
  akkaHttpCore,
  akkaActorTyped,
  akkaStream,
  akkaActor,
  slf4jNop
)

lazy val settings = Seq(
    jacocoReportSettings := JacocoReportSettings(
      "Jacoco Coverage Report",
      None,
      JacocoThresholds(),
      Seq(
        JacocoReportFormats.ScalaHTML,
        JacocoReportFormats.XML
      ), // note XML formatter
      "utf-8"
    ),
    jacocoExcludes := Seq(
      "*aview.*",
      "*fileIOComponent.*",
      "*.Uno.scala"
    ),
    jacocoCoverallsServiceName := "github-actions",
    jacocoCoverallsBranch := sys.env.get("CI_BRANCH"),
    jacocoCoverallsPullRequest := sys.env.get("GITHUB_EVENT_NAME"),
    jacocoCoverallsRepoToken := sys.env.get("COVERALLS_REPO_TOKEN")
  )

val scala3Version = "3.1.0"

lazy val root = project
  .in(file("."))
  .dependsOn(model, fileIO, dienste, card, table)
  .aggregate(dienste, model, fileIO, card, table)
  .settings(
    name := "romme",
    version := "0.1.0-SNAPSHOT",
    dockerExposedPorts := Seq(8083),
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  ).enablePlugins(JavaAppPackaging, DockerPlugin)


  lazy val model = (project in file("model"))
  .dependsOn(dienste, card, deck, table)
  .settings(
    name := "romme-model",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  )

  lazy val fileIO = (project in file("fileIO"))
  .dependsOn(model)
  .settings(
    name := "romme-fileio",
    version := "0.1.0-SNAPSHOT",
    dockerExposedPorts := Seq(8082),
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  ).enablePlugins(JavaAppPackaging, DockerPlugin)

  lazy val dienste = (project in file("dienste"))
  .settings(
    name := "romme-dienste",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  )

  lazy val card = (project in file("card"))
  .settings(
    name := "romme-card",
    version := "0.1.0-SNAPSHOT",
    dockerExposedPorts := Seq(8080),
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  ).enablePlugins(JavaAppPackaging, DockerPlugin)

  lazy val table = (project in file("table"))
  .dependsOn(card)
  .settings(
    name := "romme-table",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  )

lazy val deck = (project in file("deck"))
  .dependsOn(card)
  .settings(
    name := "romme-deck",
    version := "0.1.0-SNAPSHOT",
    dockerExposedPorts := Seq(8081),
    scalaVersion := scala3Version,
    settings,
    libraryDependencies ++= allDependencies
  ).enablePlugins(JavaAppPackaging, DockerPlugin)
