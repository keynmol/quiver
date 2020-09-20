resolvers += Resolver.url(
  "tpolecat-sbt-plugin-releases",
  url("https://dl.bintray.com/content/tpolecat/sbt-plugin-releases")
)(Resolver.ivyStylePatterns)

addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"             % "0.1.10")
addSbtPlugin("com.eed3si9n"              % "sbt-unidoc"               % "0.4.3")

addSbtPlugin("org.scalameta"             % "sbt-mdoc"                 % "2.2.9")
addSbtPlugin("org.portable-scala"        % "sbt-scalajs-crossproject" % "1.0.0")
addSbtPlugin("org.scala-js"              % "sbt-scalajs"              % "1.2.0")
addSbtPlugin("org.scalameta"             % "sbt-scalafmt"             % "2.4.0")
addSbtPlugin("com.47deg"                 % "sbt-microsites"           % "1.1.5")
addSbtPlugin("com.geirsson"              % "sbt-ci-release"           % "1.5.3")
