lazy val microservice = Project("platform-status-consul-backend", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    majorVersion        := 0,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalaVersion        := "3.3.6"
  )
  .settings(PlayKeys.playDefaultPort := 8468)
  .settings(scalacOptions ++= Seq(
    "-Wconf:msg=unused&src=.*routes/.*:s"
  , "-Wconf:msg=Flag.*repeatedly:s"
  ))
