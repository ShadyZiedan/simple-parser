ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

val http4sVersion = "0.23.9"
val CirceVersion = "0.14.1"
val LogbackVersion = "1.2.10"
val JSoupVersion = "1.14.3"

lazy val root = (project in file("."))
  .settings(
    name := "simple-parser",
    idePackagePrefix := Some("com.example.parser"),
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
      "org.jsoup" % "jsoup" % JSoupVersion,
    ),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
  )
