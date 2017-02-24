organization in ThisBuild := "uk.co.turingatemyhamster"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test

lazy val `shoppinglist` = (project in file("."))
  .aggregate(`user-api`, `user-impl`, `list-api`, `list-impl`, `web-ui`)

lazy val `user-api` = (project in file("user-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `user-impl` = (project in file("user-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`user-api`)

lazy val `list-api` = (project in file("list-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `list-impl` = (project in file("list-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`list-api`)

lazy val `web-ui` = (project in file("web-ui"))
  .enablePlugins(PlayScala, LagomPlay)
    .settings(
      version := "1.0-SNAPSHOT",
      routesGenerator := InjectedRoutesGenerator,
      libraryDependencies ++= Seq(
        "org.webjars" % "react" % "0.14.3",
        "org.webjars" % "react-router" % "1.0.3",
        "org.webjars" % "jquery" % "2.2.0",
        "org.webjars" % "foundation" % "5.3.0",
        macwire
      ),
      ReactJsKeys.sourceMapInline := true
    )