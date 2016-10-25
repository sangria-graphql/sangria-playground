name := "sangria-playground"
description := "An example of GraphQL server written with Play and Sangria."

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  filters,
  "com.beachape" %% "enumeratum" % "1.4.17",
  "org.sangria-graphql" %% "sangria" % "1.0.0-RC2",
  "org.sangria-graphql" %% "sangria-play-json" % "0.3.3",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)

herokuAppName in Compile := "sangria-playground-1"
