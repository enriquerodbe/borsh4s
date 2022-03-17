ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := "2.13.8"

ThisProject / scalacOptions += "-Xsource:3"

ThisBuild / scapegoatVersion := "1.4.12"

lazy val root = (project in file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "borsh4s",
    libraryDependencies ++= Seq(
      "com.chuusai" %%% "shapeless" % "2.3.8",
      "org.scalameta" %%% "munit" % "0.7.29" % Test
    ),
    wartremoverErrors ++= Warts.allBut(Wart.Nothing, Wart.ImplicitParameter),
    coverageFailOnMinimum := true,
    coverageMinimumStmtTotal := 100,
    coverageMinimumBranchTotal := 100
  )

addCommandAlias(
  "verify",
  ";scalafmtCheckAll ;scapegoat ;coverage ;test ;coverageReport"
)
