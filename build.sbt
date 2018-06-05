name := "sangria-playground"
description := "An example of GraphQL server written with Play and Sangria."

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  guice,
  filters,
  "org.sangria-graphql" %% "sangria" % "1.4.1",
  "org.sangria-graphql" %% "sangria-play-json" % "1.0.4",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)

herokuAppName in Compile := "sangria-playground-1"
