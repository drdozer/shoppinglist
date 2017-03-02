organization in ThisBuild := "uk.co.turingatemyhamster"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

val versions  = new {
  val scala = "2.11.8"
  val scalaDom = "0.9.1"
  val scalajsReact = "0.11.3"
  val scalaCSS = "0.5.0"
  val log4js = "1.4.10"
  val autowire = "0.2.5"
  val booPickle = "1.2.5"
  val uPickle = "0.4.3"
  val diode = "1.1.0"
  val uTest = "0.4.4"
  val sriWeb = "0.7.1"

  val react = "15.3.1"
  val jQuery = "1.11.1"
  val bootstrap = "3.3.6"
  val chartjs = "2.1.3"

  val scalajsScripts = "1.0.0"
}

val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test

lazy val `shoppinglist` = (project in file("."))
  .aggregate(`user-api`, `user-impl`, `list-api`, `list-impl`, `web-react`, `web-ui`)

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

lazy val `web-shared` = (crossProject.crossType(CrossType.Pure) in file("web-shared"))
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "autowire" % versions.autowire,
      "com.lihaoyi" %%% "upickle" % versions.uPickle
    )
  )
  .jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val `web-shared-js` = `web-shared`.js

lazy val `web-react` = (project in file("web-react"))
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .settings(
    libraryDependencies ++= Seq(
      "com.github.japgolly.scalajs-react" %%% "core" % versions.scalajsReact,
      "com.github.japgolly.scalajs-react" %%% "extra" % versions.scalajsReact,
      "com.github.japgolly.scalacss" %%% "ext-react" % versions.scalaCSS,
      "me.chrons" %%% "diode" % versions.diode,
      "me.chrons" %%% "diode-react" % versions.diode,
      "org.scala-js" %%% "scalajs-dom" % versions.scalaDom,
      "com.lihaoyi" %%% "utest" % versions.uTest % Test
    ),
    jsDependencies ++= Seq(
      "org.webjars.bower" % "react" % versions.react / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
      "org.webjars.bower" % "react" % versions.react / "react-dom.js" minified "react-dom.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOM",
      "org.webjars" % "jquery" % versions.jQuery / "jquery.js" minified "jquery.min.js",
      "org.webjars" % "bootstrap" % versions.bootstrap / "bootstrap.js" minified "bootstrap.min.js" dependsOn "jquery.js",
      "org.webjars" % "chartjs" % versions.chartjs / "Chart.js" minified "Chart.min.js",
      "org.webjars" % "log4javascript" % versions.log4js / "js/log4javascript_uncompressed.js" minified "js/log4javascript.js"
    ),
    skip in packageJSDependencies := false,
    persistLauncher := true,
    persistLauncher in Test := false,
    testFrameworks += new TestFramework("utest.runner.Framework")
  )
  .dependsOn(`web-shared-js`)

lazy val `web-ui` = (project in file("web-ui"))
  .enablePlugins(PlayScala, LagomPlay)
  .settings(
    routesGenerator := InjectedRoutesGenerator,
    libraryDependencies ++= Seq(
      "com.vmunier" %% "scalajs-scripts" % versions.scalajsScripts,
      "org.webjars" % "react" % "0.14.3",
      "org.webjars" % "react-router" % "1.0.3",
      "org.webjars" % "jquery" % "2.2.0",
      "org.webjars" % "foundation" % "5.3.0",
      "org.webjars" % "font-awesome" % "4.3.0-1" % Provided,
      "org.webjars" % "bootstrap" % versions.bootstrap % Provided,

      macwire
    ),
    ReactJsKeys.sourceMapInline := true,
    compile in Compile <<= (compile in Compile) dependsOn scalaJSPipeline,
    scalaJSProjects := Seq(`web-react`),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    LessKeys.compress in Assets := true
  )