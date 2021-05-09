name := """sklep"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.5"

ThisBuild / useJCenter := true

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.34.0"
libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.0.0"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0"
libraryDependencies += evolutions
libraryDependencies += "io.github.nafg" %% "slick-additions" % "0.11.1"
libraryDependencies += "io.github.nafg" %% "slick-additions-entity" % "0.11.1"

// Adds additional packages into Twirl
TwirlKeys.templateImports += "helpers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"

