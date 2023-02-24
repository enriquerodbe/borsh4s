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
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
    crossScalaVersions := Seq("3.2.1"),
    scalaVersion := "3.2.1",
    githubWorkflowTargetBranches := Seq("main"),
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
    githubWorkflowJavaVersions := Seq(JavaSpec.temurin("17.0.4")),
    githubWorkflowBuild := Seq(
      WorkflowStep
        .Sbt(
          name = Some("Lint"),
          commands =
            List("scalafmtSbtCheck", "scalafmtCheckAll", "borsh4sJVM/compile")
        ),
      WorkflowStep.Sbt(
        name = Some("Test"),
        commands = List("borsh4sJS/test")
      ),
      WorkflowStep.Sbt(
        name = Some("Coverage report"),
        commands =
          List("coverage", "borsh4sJVM/test", "borsh4sJVM/coverageReport")
      )
    )
  )
)

ThisProject / publish / skip := true

lazy val borsh4s =
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .in(file("."))
    .settings(
      name := "borsh4s",
      libraryDependencies ++= Seq(
        "com.softwaremill.magnolia1_3" %%% "magnolia" % "1.2.6",
        "org.scalameta" %%% "munit" % "0.7.29" % Test
      ),
      Compile / compile / wartremoverErrors ++=
        Warts.allBut(Wart.Any, Wart.Nothing),
      coverageFailOnMinimum := true,
      coverageMinimumStmtTotal := 100,
      coverageMinimumBranchTotal := 100
    )
    .jsSettings(
      libraryDependencies +=
        ("org.scala-js" %%% "scalajs-java-securerandom" % "1.0.0")
          .cross(CrossVersion.for3Use2_13)
    )
