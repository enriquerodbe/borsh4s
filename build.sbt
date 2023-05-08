ThisProject / publish / skip := true

lazy val borsh4s =
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("."))
    .settings(
      name := "borsh4s",
      scalaVersion := "3.2.1",

      // Publish config
      organization := "io.github.enriquerodbe",
      homepage := Some(url("https://github.com/enriquerodbe/borsh4s")),
      licenses := Seq(License.CC0),
      developers := List(
        Developer(
          "enriquerodbe",
          "Enrique Rodríguez",
          "enriquerodbe@gmail.com",
          url("https://github.com/enriquerodbe")
        )
      ),
      versionScheme := Some(VersionScheme.EarlySemVer),
      sonatypeCredentialHost := "s01.oss.sonatype.org",
      sonatypeRepository := "https://s01.oss.sonatype.org/service/local",

      // Dependencies
      libraryDependencies ++= Seq(
        "com.softwaremill.magnolia1_3" %%% "magnolia" % "1.2.6",
        "org.scalameta" %%% "munit" % "0.7.29" % Test
      ),

      // Lint config
      Compile / compile / wartremoverErrors ++=
        Warts.allBut(Wart.Any, Wart.Nothing),
      coverageFailOnMinimum := true,
      // There are 2 lines in Borsh4s.scala that don't get reported
      coverageMinimumStmtTotal := 98.78,
      coverageMinimumBranchTotal := 100
    )
    .jsSettings(
      libraryDependencies +=
        ("org.scala-js" %%% "scalajs-java-securerandom" % "1.0.0")
          .cross(CrossVersion.for3Use2_13)
    )
