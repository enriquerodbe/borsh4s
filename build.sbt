lazy val root =
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("."))
    .settings(
      organization := "io.borsh4s",
      name := "borsh4s",
      version := "0.0.1",
      licenses := Seq(License.CC0),
      scalaVersion := "2.13.8",
      scalacOptions += "-Xsource:3",
      libraryDependencies ++= Seq(
        "com.chuusai" %%% "shapeless" % "2.3.9",
        "org.scalameta" %%% "munit" % "0.7.29" % Test
      ),
      Compile / compile / wartremoverErrors ++=
        Warts.allBut(Wart.Nothing, Wart.ImplicitParameter),
      ThisBuild / scapegoatVersion := "1.4.12",
      coverageFailOnMinimum := true,
      coverageMinimumStmtTotal := 100,
      coverageMinimumBranchTotal := 100
    )
