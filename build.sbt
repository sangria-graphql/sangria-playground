name := "sangria-playground"
description := "An example of GraphQL server written with Play and Sangria."

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  filters,
  "org.sangria-graphql" %% "sangria" % "1.2.1",
  "org.sangria-graphql" %% "sangria-play-json" % "1.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)

herokuAppName in Compile := "sangria-playground-1"
