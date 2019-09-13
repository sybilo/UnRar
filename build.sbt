name := "UnRar"

version := "0.1"

scalaVersion := "2.12.6"

lazy val akkaVersion = "2.5.25"

unmanagedBase := baseDirectory.value / "libs"

offline := true

resolvers += "jitpack" at "https://jitpack.io"
libraryDependencies ++= Seq(
  //  "net.lingala.zip4j" % "zip4j" % "[1.3.2,)",
  //  "com.github.junrar" % "junrar" % "[4.0.0,)",

  "com.github.dedge-space" % "bitcoinj" % "33b5d94bfc",
  "com.github.dedge-space" % "annoguard" % "1.0.3-beta",
  "com.github.dedge-space" % "scala-lang" % "cc6be80562",
  "com.github.dedge-space" % "reflow" % "a2d3ecea44",

  "com.squareup.okhttp3" % "okhttp" % "[3.11.0,)",

  //  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  //  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
