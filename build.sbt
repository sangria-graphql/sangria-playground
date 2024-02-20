name := "sangria-playground"
description := "An example of GraphQL server written with Play and Sangria."

version := "1.0-SNAPSHOT"

scalaVersion := "2.13.12"

libraryDependencies ++= Seq(
  guice,
  filters,
  "ch.qos.logback" % "logback-classic" % "1.5.0",
  "org.sangria-graphql" %% "sangria" % "3.5.3",
  "org.sangria-graphql" %% "sangria-slowlog" % "2.0.5",
  "org.sangria-graphql" %% "sangria-play-json" % "2.0.2",
  "org.scalatest" %% "scalatest" % "3.2.17" % "test")

lazy val root = (project in file(".")).enablePlugins(PlayScala)
