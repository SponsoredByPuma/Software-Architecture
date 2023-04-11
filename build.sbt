
val scala3Version = "3.1.0"

lazy val root = project
  .in(file("."))
  .dependsOn(model, fileIO, dienste)
  .aggregate(dienste, model, fileIO)
  .settings(
    name := "romme",
    version := "0.1.0-SNAPSHOT",
    commonSettings,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.10",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test",
    libraryDependencies += ("org.scala-lang.modules" %% "scala-swing" % "3.0.0")
      .cross(CrossVersion.for3Use2_13),
    libraryDependencies += ("com.google.inject" % "guice" % "4.2.3"),
    libraryDependencies += ("net.codingwell" %% "scala-guice" % "5.0.2")
      .cross(CrossVersion.for3Use2_13),
    libraryDependencies += ("org.scala-lang.modules" %% "scala-xml" % "2.0.1"),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % "0.14.1"),
    libraryDependencies += ("com.typesafe.play" %% "play-json" % "2.9.2")
      .cross(CrossVersion.for3Use2_13),
  )
  .enablePlugins(JacocoCoverallsPlugin)


  lazy val model = (project in file("model"))
  .dependsOn(dienste)
  .settings(
    name := "Romme-Model",
    version := "0.1.0-SNAPSHOT",
    commonSettings,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.10",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test",
    libraryDependencies += ("org.scala-lang.modules" %% "scala-swing" % "3.0.0")
      .cross(CrossVersion.for3Use2_13),
    libraryDependencies += ("com.google.inject" % "guice" % "4.2.3"),
    libraryDependencies += ("net.codingwell" %% "scala-guice" % "5.0.2")
      .cross(CrossVersion.for3Use2_13),
    libraryDependencies += ("org.scala-lang.modules" %% "scala-xml" % "2.0.1"),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % "0.14.1"),
    libraryDependencies += ("com.typesafe.play" %% "play-json" % "2.9.2")
      .cross(CrossVersion.for3Use2_13),
  )

  lazy val fileIO = (project in file("fileIO"))
  .dependsOn(model)
  .settings(
    name := "Romme-FileIO",
    version := "0.1.0-SNAPSHOT",
    commonSettings,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.10",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test",
    libraryDependencies += ("org.scala-lang.modules" %% "scala-swing" % "3.0.0")
      .cross(CrossVersion.for3Use2_13),
    libraryDependencies += ("com.google.inject" % "guice" % "4.2.3"),
    libraryDependencies += ("net.codingwell" %% "scala-guice" % "5.0.2")
      .cross(CrossVersion.for3Use2_13),
    libraryDependencies += ("org.scala-lang.modules" %% "scala-xml" % "2.0.1"),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % "0.14.1"),
    libraryDependencies += ("com.typesafe.play" %% "play-json" % "2.9.2")
      .cross(CrossVersion.for3Use2_13),
  )

  lazy val dienste = (project in file("dienste"))
  .settings(
    name := "Romme-Dienste",
    version := "0.1.0-SNAPSHOT",
    commonSettings,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.10",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test",
    libraryDependencies += ("org.scala-lang.modules" %% "scala-swing" % "3.0.0")
      .cross(CrossVersion.for3Use2_13),
    libraryDependencies += ("com.google.inject" % "guice" % "4.2.3"),
    libraryDependencies += ("net.codingwell" %% "scala-guice" % "5.0.2")
      .cross(CrossVersion.for3Use2_13),
    libraryDependencies += ("org.scala-lang.modules" %% "scala-xml" % "2.0.1"),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % "0.14.1"),
    libraryDependencies += ("com.typesafe.play" %% "play-json" % "2.9.2")
      .cross(CrossVersion.for3Use2_13),
  )

  lazy val commonSettings = Seq(
  scalaVersion := scala3Version,
  organization := "de.htwg.se",

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
    jacocoCoverallsServiceName := "github-actions",
    jacocoCoverallsBranch := sys.env.get("CI_BRANCH"),
    jacocoCoverallsPullRequest := sys.env.get("GITHUB_EVENT_NAME"),
    jacocoCoverallsRepoToken := sys.env.get("COVERALLS_REPO_TOKEN"),
    jacocoExcludes := Seq("*aview.*")
  )
