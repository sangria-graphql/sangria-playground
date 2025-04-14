name := "sangria-playground"
description := "An example of GraphQL server written with Play and Sangria."

version := "1.0-SNAPSHOT"

scalaVersion := "2.13.14"

libraryDependencies ++= Seq(
  guice,
  filters,
  "ch.qos.logback" % "logback-classic" % "1.5.7",
  "org.sangria-graphql" %% "sangria" % "4.1.1",
  "org.sangria-graphql" %% "sangria-slowlog" % "3.0.0",
  "org.sangria-graphql" %% "sangria-play-json" % "2.0.2",
  "org.scalatest" %% "scalatest" % "3.2.19" % "test")

lazy val root = (project in file(".")).enablePlugins(PlayScala)
