lazy val quiver = project
  .in(file("."))
  .aggregate(core.jvm, core.js, codecs.jvm, codecs.js, docs)

val CatsVersion = "2.2.0"
val SilencerVersion = "1.7.1"
val ScalacheckShapeless = "1.2.5"
val CollectionsCompat = "2.2.0"

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-free" % CatsVersion,
      "org.scala-lang.modules" %%% "scala-collection-compat" % CollectionsCompat,
      "org.typelevel" %%% "cats-laws" % CatsVersion % Test,
      "com.github.alexarchambault" %%% "scalacheck-shapeless_1.14" % ScalacheckShapeless % Test
    ),
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
  .settings(commonSettings)

val commonSettings = Seq(
  organization in Global := "io.verizon.quiver",
  scalaVersion in Global := crossScalaVersions.value.head,
  crossScalaVersions in Global := Seq("2.13.3", "2.12.11")
)

lazy val codecs = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("codecs"))
  .dependsOn(core % "test->test;compile->compile")
  .settings(
    libraryDependencies += "org.scodec" %%% "scodec-core" % "1.11.7"
  )
  .settings(commonSettings)

lazy val docs = project
  .in(file("quiver-docs"))
  .dependsOn(core.jvm, codecs.jvm)
  .enablePlugins(MdocPlugin)
  .settings(
    mdocIn := (baseDirectory in ThisBuild).value / "docs" / "mdoc"
  )
