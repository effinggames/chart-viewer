name := "chart-viewer"

version := "1.0"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(SbtWeb)
  .settings(PlayKeys.playDefaultPort := 8000)

scalaVersion := "2.11.8"

libraryDependencies ++=  Seq(
  ws % Test, // only used in tests right now
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.softwaremill.macwire" %% "macros" % "2.2.2" % "provided",
  "com.softwaremill.macwire" %% "util" % "2.2.2",
  "com.softwaremill.macwire" %% "proxy" % "2.2.2",
  "io.getquill" %% "quill-jdbc" % "1.0.0",
  "org.postgresql" % "postgresql" % "9.4.1208",
  "org.scala-lang.modules" %% "scala-async" % "0.9.5"
)

lazy val npmBuildTask = taskKey[Unit]("Execute the npm build command to build the ui")

npmBuildTask := {
  "npm run build".!
}

//Triggers build/watching for development mode.
PlayKeys.playRunHooks += NpmTask("install")
PlayKeys.playRunHooks += NpmTask("run build-dev")

//Npm build during production compiling.
compile := ((compile in Compile) dependsOn npmBuildTask).value

//Compile steps for production mode.
pipelineStages := Seq(digest, gzip)
