import sbt.ScriptedPlugin._
import _root_.bintray.BintrayPlugin.bintrayPublishSettings

val baseVersion = "5.1.1-SNAPSHOT"
val maxMetaspaceSize = if (util.Properties.isJavaAtLeast("1.8")) {
  "-XX:MaxMetaspaceSize=384m"
} else {
  "-XX:MaxPermSize=384m"
}

lazy val root = Project(
  "sbteclipse-plugin",
  file("."),
  settings = commonSettings ++ Seq(
    libraryDependencies ++= Seq(
      "org.scalaz"    %% "scalaz-core"   % "7.2.14",
      "org.scalaz"    %% "scalaz-effect" % "7.2.14",
      "org.scalatest" %% "scalatest"     % "3.0.1" % "test")
  )
)

def commonSettings =
  versionWithGit ++
  scriptedSettings ++
  sbtrelease.ReleasePlugin.projectSettings ++
  bintrayPublishSettings ++
  Seq(
    git.baseVersion := baseVersion,
    organization := "com.typesafe.sbteclipse",
    // version is defined in version.sbt in order to support sbt-release
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    sbtPlugin := true,
    publishMavenStyle := false,
    sbtVersion in GlobalScope := {
      System.getProperty("sbt.build.version", (sbtVersion in GlobalScope).value)
    },
    scalaVersion := {
      (sbtVersion in GlobalScope).value match {
        case sbt013 if sbt013.startsWith("0.13.") => "2.10.6"
        case sbt012 if sbt012.startsWith("0.12.") => "2.9.3"
        case _ => "2.10.6"
      }
    },
    sbtDependency in GlobalScope := {
      (sbtDependency in GlobalScope).value.copy(revision = (sbtVersion in GlobalScope).value)
    },
    publishMavenStyle := false,
    bintrayOrganization := Some("sbt"),
    bintrayPackage := "sbteclipse",
    bintrayRepository := "sbt-plugin-releases",
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Compile, packageSrc) := false,
    // Uncomment the following line to get verbose output
    scriptedBufferLog := false,
    scriptedLaunchOpts ++= List(maxMetaspaceSize, "-Dplugin.version=" + version.value)
  )
