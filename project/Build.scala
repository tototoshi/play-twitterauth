import com.typesafe.sbt.SbtScalariform.scalariformSettings
import sbt._
import Keys._

object ApplicationBuild extends Build {

  lazy val plugin = Project (
    id = "plugin",
    base = file ("plugin")
  ).settings(
    Seq(
      name := "play-twitterauth",
      organization := "com.github.tototoshi",
      version := "0.1.0-SNAPSHOT",
      scalaVersion := "2.11.2",
      crossScalaVersions := scalaVersion.value :: "2.10.4" :: Nil,
      resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      libraryDependencies ++= Seq(
        "com.typesafe.play" %% "play" % play.core.PlayVersion.current % "provided",
        "com.typesafe.play" %% "play-ws" % play.core.PlayVersion.current % "provided",
        "org.twitter4j" % "twitter4j-core" % "4.0.2" % "provided"
      ),
      scalacOptions ++= Seq("-language:_", "-deprecation")
    ) ++ scalariformSettings ++ publishingSettings :_*
  )

  val playAppName = "playapp"
  val playAppVersion = "1.0-SNAPSHOT"

  lazy val playapp = Project(
    playAppName,
    file("playapp")
  ).enablePlugins(play.PlayScala).settings(scalariformSettings:_*)
  .settings(
    resourceDirectories in Test += baseDirectory.value / "conf",
    scalaVersion := "2.11.2",
    version := playAppVersion,
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play" % play.core.PlayVersion.current,
      "com.typesafe.play" %% "play-ws" % play.core.PlayVersion.current,
      "org.twitter4j" % "twitter4j-core" % "4.0.2"
    )
  )
  .dependsOn(plugin)

  val publishingSettings = Seq(
    publishMavenStyle := true,
    publishTo <<= version { (v: String) => _publishTo(v) },
    publishArtifact in Test := false,
    pomExtra := _pomExtra
  )

  def _publishTo(v: String) = {
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }

  val _pomExtra =
    <url>http://github.com/tototoshi/play-twitterauth</url>
    <licenses>
      <license>
        <name>Apache License, Version 2.0</name>
        <url>http://github.com/tototoshi/play-twitterauth/blob/master/LICENSE.txt</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:tototoshi/play-twitterauth.git</url>
      <connection>scm:git:git@github.com:tototoshi/play-twitterauth.git</connection>
    </scm>
    <developers>
      <developer>
        <id>tototoshi</id>
        <name>Toshiyuki Takahashi</name>
        <url>http://tototoshi.github.com</url>
      </developer>
    </developers>

}
