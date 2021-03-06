import sbt._

lazy val githubstats = (project in file(".")).
  settings (
    name := "GithubStats",
    organization := "net.sigusr",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.13.1"
    // add other settings here
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
  //,"-Ypartial-unification"
  //,"-Xfatal-warnings" // Recommend enable before you commit code
  , "-language:_"
  //,"-optimise"
)

javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-source", "1.7", "-target", "1.7")

// javaOptions in Universal ++= Seq(
//   "-J-server",
//   "-J-Xms1g -Xmx4g",
//   "-J-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled",
//   "-J-XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=68",
//   "-J-XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBeforeRemark",
//   "-J-XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M"
// )

val CatsVersion = "2.1.0"
val CatsEffectVersion = "2.1.1"
val Console4CatsVersion = "0.8.1"
val MonixVersion = "3.1.0"
val ZIOVersion = "1.0.0-RC17"
val ShapelessVersion = "2.3.3"
val FS2Version = "2.2.2"
val AmmoniteVersion = "2.0.0"
val Http4sVersion = "0.21.0"
val CirisVersion = "1.0.4"

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
  "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
  "org.http4s" %% "http4s-circe" % Http4sVersion,

  // Optional for auto-derivation of JSON codecs
  "io.circe" %% "circe-generic" % "0.13.0",

  // Optional for string interpolation to JSON model
  "io.circe" %% "circe-literal" % "0.13.0",
  "io.circe" %% "circe-parser" % "0.13.0",

  // fs2
  "co.fs2" %% "fs2-core" % FS2Version,
  // monix
  "io.monix" %% "monix" % MonixVersion,
  // shapeless
  "com.chuusai" %% "shapeless" % ShapelessVersion,
  // scalaz
  "dev.zio" %% "zio" % ZIOVersion,
  "dev.zio" %% "zio-streams" % ZIOVersion,
  // type classes
  "com.github.mpilquist" %% "simulacrum" % "0.19.0",
  // li haoyi ammonite repl embed
  "com.lihaoyi" % "ammonite" % AmmoniteVersion % "test" cross CrossVersion.full
)

//ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

resolvers ++= Seq(
  "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "Secured Central Repository" at "https://repo1.maven.org/maven2",
  Resolver.sonatypeRepo("snapshots")
)

// ammonite repl
//sourceGenerators in Test += Def.task {
//  val file = (sourceManaged in Test).value / "amm.scala"
//  IO.write(file, """object amm extends App { ammonite.Githubstats().run() }""")
//  Seq(file)
//}.taskValue

