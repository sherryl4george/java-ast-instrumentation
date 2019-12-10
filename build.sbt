name := "cs474.cp"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  //Scala config
  "com.typesafe" % "config" % "1.3.4",

  //Scala logging
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "org.gnieh" % "logback-config" % "0.4.0",

  //Scala testing
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",

  // https://mvnrepository.com/artifact/org.eclipse.jdt/org.eclipse.jdt.core
  "org.eclipse.jdt" % "org.eclipse.jdt.core" % "3.19.0",

  // https://mvnrepository.com/artifact/commons-io/commons-io
  "commons-io" % "commons-io" % "2.6",

  // https://mvnrepository.com/artifact/org.apache.ant/ant
  "org.apache.ant" % "ant" % "1.10.7",

  // https://mvnrepository.com/artifact/com.typesafe.akka/akka-http
  "com.typesafe.akka" %% "akka-http" % "10.1.10",
  // https://mvnrepository.com/artifact/com.typesafe.akka/akka-stream
  "com.typesafe.akka" %% "akka-stream" % "2.6.0",

  // https://mvnrepository.com/artifact/com.typesafe.play/play-json
  "com.typesafe.play" %% "play-json" % "2.8.0",

  "org.apache.commons" % "commons-lang3" % "3.9"

)



