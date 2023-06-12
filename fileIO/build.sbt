name := "Romme-FileIO"
organization  := "de.htwg.se"
version       := "0.1.0-SNAPSHOT"
scalaVersion := "3.1.0"

lazy val commonDependencies = Seq(
    "org.scalactic" %% "scalactic" % "3.2.10",
    "org.scalatest" %% "scalatest" % "3.2.10" % "test",
    ("org.scala-lang.modules" %% "scala-swing" % "3.0.0")
      .cross(CrossVersion.for3Use2_13),
    ("com.typesafe.akka" %% "akka-actor-typed" % "2.7.0")
      .cross(CrossVersion.for3Use2_13),
    ("com.typesafe.akka" %% "akka-stream" % "2.7.0")
      .cross(CrossVersion.for3Use2_13),
    ("com.typesafe.akka" %% "akka-http" % "10.5.1")
      .cross(CrossVersion.for3Use2_13),
    ("com.typesafe.akka" %% "akka-actor" % "2.7.0")
      .cross(CrossVersion.for3Use2_13),
    ("com.google.inject" % "guice" % "4.2.3"),
    ("net.codingwell" %% "scala-guice" % "5.0.2")
      .cross(CrossVersion.for3Use2_13),
    ("org.scala-lang.modules" %% "scala-xml" % "2.0.1"),
    ("com.typesafe.play" %% "play-json" % "2.9.2")
      .cross(CrossVersion.for3Use2_13),
)
libraryDependencies += "de.htwg.se" % "romme-card_3" % "0.1.0-SNAPSHOT"
libraryDependencies += "de.htwg.se" % "romme-deck_3" % "0.1.0-SNAPSHOT"
libraryDependencies += "de.htwg.se" % "romme-model_3" % "0.1.0-SNAPSHOT"
libraryDependencies += "de.htwg.se" % "romme-table_3" % "0.1.0-SNAPSHOT"

libraryDependencies ++= commonDependencies
libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % "0.14.1")
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.32"
libraryDependencies += ("org.mongodb.scala" %% "mongo-scala-driver" % "4.6.0").cross(CrossVersion.for3Use2_13)