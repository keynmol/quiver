import java.net.URL
lazy val quiver = project
  .in(file("."))
  .aggregate(core.jvm, core.js, codecs.jvm, codecs.js, docs)
  .settings(
    skip in publish := true
  )

val CatsVersion         = "2.2.0"
val SilencerVersion     = "1.7.1"
val ScalacheckShapeless = "1.2.5"
val CollectionsCompat   = "2.2.0"

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel"              %%% "cats-free"                 % CatsVersion,
      "org.scala-lang.modules"     %%% "scala-collection-compat"   % CollectionsCompat,
      "org.typelevel"              %%% "cats-laws"                 % CatsVersion         % Test,
      "com.github.alexarchambault" %%% "scalacheck-shapeless_1.14" % ScalacheckShapeless % Test
    )
  )
  .settings(commonSettings)
  .settings(silenceCompat)

val commonSettings = Seq(
  organization in Global := "io.getnelson.quiver",
  scalaVersion in Global := crossScalaVersions.value.head,
  crossScalaVersions in Global := Seq("2.13.3", "2.12.11"),
  // this is temporary, to restore v-prefixed tags in master branch
  // after that we can re-enable
  dynverVTagPrefix in ThisBuild := false,
  developers := List(
    Developer(
      "timperrett",
      "Timothy Perrett",
      "",
      new URL("http://github.com/timperrett")
    ),
    Developer(
      "runorama",
      "Runar Bjarnason",
      "",
      new URL("https://github.com/runarorama")
    ),
    Developer("stew", "Stew O'Connor", "", new URL("http://github.com/stew"))
  ),
  licenses := Seq(
    "Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")
  ),
  homepage := Some(url("http://getnelson.github.io/quiver/")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/getnelson/quiver"),
      "git@github.com:getnelson/quiver.git"
    )
  )
)

val silenceCompat = Seq(
  libraryDependencies ++= Seq(
    compilerPlugin(
      "com.github.ghik" % "silencer-plugin" % SilencerVersion cross CrossVersion.full
    ),
    "com.github.ghik" % "silencer-lib" % SilencerVersion % Provided cross CrossVersion.full
  ),
  scalacOptions in Compile ++= Seq(
    """-P:silencer:lineContentFilters=import scala\.collection\.compat\._"""
  )
)

lazy val codecs = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("codecs"))
  .dependsOn(core % "test->test;compile->compile")
  .settings(
    libraryDependencies += "org.scodec" %%% "scodec-core" % "1.11.7"
  )
  .settings(commonSettings)

lazy val docsMappingsAPIDir = settingKey[String](
  "Name of subdirectory in site target directory for api docs"
)
lazy val docs = project
  .in(file("quiver-docs"))
  .dependsOn(core.jvm, codecs.jvm)
  .enablePlugins(MdocPlugin, MicrositesPlugin, ScalaUnidocPlugin)
  .settings(
    unidocProjectFilter in (ScalaUnidoc, unidoc) := inProjects(
      core.jvm,
      codecs.jvm
    ),
    docsMappingsAPIDir := "api",
    addMappingsToSiteDir(
      mappings in (ScalaUnidoc, packageDoc),
      docsMappingsAPIDir
    ),
    mdocVariables := {
      val stableVersion: String =
        version.value.replaceFirst("\\+.*", "")
      Map("VERSION" -> stableVersion)
    },
    mdocIn := (baseDirectory in ThisBuild).value / "docs" / "mdoc",
    micrositeName := "Quiver - a Scala graph library",
    micrositeUrl := "https://getnelson.github.io",
    micrositeDocumentationUrl := "/quiver/api/index.html",
    micrositeDocumentationLabelDescription := "API Documentation",
    micrositeBaseUrl := "/quiver"
  )
  .settings(silenceCompat)
