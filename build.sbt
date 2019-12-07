name := "cs474.cp_test"

version := "0.1"

scalaVersion := "2.13.1"

// https://mvnrepository.com/artifact/org.eclipse.jdt/org.eclipse.jdt.core
libraryDependencies += "org.eclipse.jdt" % "org.eclipse.jdt.core" % "3.19.0"
// https://mvnrepository.com/artifact/commons-io/commons-io
libraryDependencies ++=  Seq("commons-io" % "commons-io" % "2.6",
  "com.typesafe" % "config" % "1.3.4",

  //Scala logging
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "org.gnieh" % "logback-config" % "0.4.0",

  //ANT Building
  "org.apache.ant" % "ant" % "1.10.7",

  //Scala testing
  "org.scalatest" %% "scalatest" % "3.0.8" % "test")



