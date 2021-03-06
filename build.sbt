name := """sklep"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.5"

ThisBuild / useJCenter := true
bspEnabled := false

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.34.0"
libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.0.0"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0"
libraryDependencies += "com.byteslounge" %% "slick-repo" % "1.6.1"

libraryDependencies ++= Seq(
		"org.webjars.npm" % "normalize.css" % "8.0.1",
		"org.webjars.npm" % "milligram" % "1.3.0",
)

libraryDependencies ++= Seq(
	"com.mohiva" %% "play-silhouette" % "7.0.0",
	"com.mohiva" %% "play-silhouette-password-bcrypt" % "7.0.0",
	"com.mohiva" %% "play-silhouette-crypto-jca" % "7.0.0",
	"com.mohiva" %% "play-silhouette-persistence" % "7.0.0",
)

libraryDependencies += ehcache
libraryDependencies += "com.iheart" %% "ficus" % "1.5.0"
libraryDependencies += "net.codingwell" %% "scala-guice" % "5.0.1"

// Adds additional packages into Twirl
TwirlKeys.templateImports += "helpers._"
TwirlKeys.templateImports += "components._"
TwirlKeys.templateImports += "views.html.helper._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"

// no doc in dist
Compile / doc / sources := Seq.empty
Compile / packageDoc / publishArtifact := false
// auto-reload
Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / assemblyMergeStrategy := {
	// case PathList("javax", "servlet", xs @ _*) => MergeStrategy.first
	// case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
	// case "application.conf" => MergeStrategy.concat
	// case "unwanted.txt" => MergeStrategy.discard
	case "messages" => MergeStrategy.concat
	case "play/reference-overrides.conf" => MergeStrategy.concat
	case "module-info.class" => MergeStrategy.discard
	case x =>
		val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
		oldStrategy(x)
}
