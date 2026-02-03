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
    versionScheme := Some(VersionScheme.EarlySemVer)
  )
)

lazy val borsh4s =
  crossProject(JVMPlatform, JSPlatform, NativePlatform)
    .crossType(CrossType.Pure)
    .in(file("."))
    .settings(
      name := "borsh4s",
      scalaVersion := "3.8.1",

      // Dependencies
      libraryDependencies ++= Seq(
        "com.softwaremill.magnolia1_3" %%% "magnolia" % "1.3.18",
        "org.scalameta" %%% "munit" % "1.2.2" % Test
      ),

      // Lint config
      tpolecatExcludeOptions += ScalacOptions.fatalWarnings,
      tpolecatScalacOptions ++= Set(
        ScalacOptions.warnError,
        ScalacOptions.languageFeatureOption("strictEquality"),
        ScalacOptions.maxInlines(64)
      ),
      // Disabled Wart.Equals because strictEquality is already enabled
      Compile / compile / wartremoverErrors ++=
        Warts.allBut(Wart.Any, Wart.Nothing, Wart.Equals) ++ ContribWart.All,
      coverageFailOnMinimum := true,
      // The exports in Borsh4s.scala don't get reported
      coverageMinimumStmtTotal := 99.35,
      coverageMinimumBranchTotal := 100
    )
