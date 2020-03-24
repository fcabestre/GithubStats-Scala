import sbt._

lazy val githubstats = (project in file(".")).
  settings (
    name := "GithubStats",
    organization := "net.sigusr",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.13.1"
  )

/* scala versions and options */
scalaVersion := "2.13.1"

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)

// These options will be used for *all* versions.
scalacOptions ++= Seq(
  "-deprecation"
  , "-unchecked"
  , "-encoding", "UTF-8"
  , "-Xlint"
  , "-Xverify"
  , "-feature"
  , "-language:_"
)

javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-source", "1.7", "-target", "1.7")

val CatsVersion = "2.1.0"
val CatsEffectVersion = "2.1.1"
val Console4CatsVersion = "0.8.1"
val MonixVersion = "3.1.0"
val Http4sVersion = "0.21.0"
val CirisVersion = "1.0.4"
val CirceVersion = "0.13.0"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.1",
  // -- testing --
  "org.scalacheck" %% "scalacheck" % "1.14.3" % "test",
  "org.scalatest" %% "scalatest" % "3.1.1" % "test",
  // -- Logging --
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",

  "io.estatico" %% "newtype" % "0.4.3",
  "eu.timepit" %% "refined" % "0.9.13",
  "eu.timepit" %% "refined-cats" % "0.9.13",

  // Cats
  "org.typelevel" %% "cats-core" % CatsVersion,
  "org.typelevel" %% "cats-effect" % CatsEffectVersion,
  "dev.profunktor" %% "console4cats" % Console4CatsVersion,
  "is.cir" %% "ciris" % CirisVersion,

  "org.http4s" %% "http4s-dsl" % Http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
  "org.http4s" %% "http4s-circe" % Http4sVersion,

  // Optional for auto-derivation of JSON codecs
  "io.circe" %% "circe-generic" % CirceVersion,

  // Optional for string interpolation to JSON model
  "io.circe" %% "circe-literal" % CirceVersion,
  "io.circe" %% "circe-parser" % CirceVersion,

  // monix
  "io.monix" %% "monix" % MonixVersion,
)

resolvers ++= Seq(
  "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "Secured Central Repository" at "https://repo1.maven.org/maven2",
  Resolver.sonatypeRepo("snapshots")
)

