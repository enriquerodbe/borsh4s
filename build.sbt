import org.typelevel.scalacoptions.ScalacOptions

ThisProject / publish / skip := true

// Publish config
inThisBuild(
  Seq(
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
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
  )
)

lazy val borsh4s =
  crossProject(JVMPlatform, JSPlatform, NativePlatform)
    .crossType(CrossType.Pure)
    .in(file("."))
    .settings(
      name := "borsh4s",
      scalaVersion := "3.7.0",

      // Dependencies
      libraryDependencies ++= Seq(
        "com.softwaremill.magnolia1_3" %%% "magnolia" % "1.3.16",
        "org.scalameta" %%% "munit" % "1.1.0" % Test
      ),

      // Lint config
      tpolecatScalacOptions +=
        ScalacOptions.languageFeatureOption("strictEquality"),
      Compile / compile / wartremoverErrors ++=
        Warts.allBut(Wart.Any, Wart.Nothing),
      coverageFailOnMinimum := true,
      // There are 2 lines in Borsh4s.scala that don't get reported
      coverageMinimumStmtTotal := 98.76,
      coverageMinimumBranchTotal := 100
    )
