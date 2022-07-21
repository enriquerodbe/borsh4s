inThisBuild(
  Seq(
    organization := "io.github.enriquerodbe",
    homepage := Some(url("https://github.com/enriquerodbe/borsh4s")),
    licenses := List(
      "CC0" -> url(
        "https://creativecommons.org/publicdomain/zero/1.0/legalcode"
      )
    ),
    developers := List(
      Developer(
        "enriquerodbe",
        "Enrique Rodríguez",
        "enriquerodbe@gmail.com",
        url("https://github.com/enriquerodbe")
      )
    ),
    versionScheme := Some("early-semver"),
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
    crossScalaVersions := Seq("2.13.8"),
    scalaVersion := "2.13.8",
    githubWorkflowTargetTags ++= Seq("v*"),
    githubWorkflowPublishTargetBranches := Seq(
      RefPredicate.StartsWith(Ref.Tag("v"))
    ),
    githubWorkflowPublish := Seq(
      WorkflowStep.Sbt(
        commands = List("ci-release"),
        env = Map(
          "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
          "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
          "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
          "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
        )
      )
    ),
    githubWorkflowJavaVersions := Seq(JavaSpec.temurin("17")),
    githubWorkflowBuild := Seq(
      WorkflowStep
        .Sbt(
          name = Some("Lint"),
          commands =
            List("scalafmtSbtCheck", "scalafmtCheckAll", "rootJVM/scapegoat")
        ),
      WorkflowStep.Sbt(
        name = Some("Test"),
        commands = List("coverage", "rootJVM/test", "rootJS/test")
      ),
      WorkflowStep.Sbt(
        name = Some("Coverage report"),
        commands = List("rootJVM/coverageReport", "rootJS/coverageReport")
      )
    )
  )
)

ThisProject / skip := true

lazy val root =
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("."))
    .settings(
      name := "borsh4s",
      licenses := Seq(License.CC0),
      scalacOptions += "-Xsource:3",
      libraryDependencies ++= Seq(
        "com.chuusai" %%% "shapeless" % "2.3.9",
        "org.scalameta" %%% "munit" % "0.7.29" % Test
      ),
      Compile / compile / wartremoverErrors ++=
        Warts.allBut(Wart.Nothing, Wart.ImplicitParameter),
      ThisBuild / scapegoatVersion := "1.4.15",
      coverageFailOnMinimum := true,
      coverageMinimumStmtTotal := 100,
      coverageMinimumBranchTotal := 100
    )
    .jsSettings(
      libraryDependencies ++= Seq(
        "org.scala-js" %%% "scalajs-java-securerandom" % "1.0.0"
      )
    )
