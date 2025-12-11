val scala3Version = "3.4.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "WebScraper",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.2.14",
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "org.scalafx" %% "scalafx" % "21.0.0-R32",  // Use ScalaFX 21 for Java 21

      // Explicitly add JavaFX 21 dependencies
      "org.openjfx" % "javafx-controls" % "21.0.2" classifier "linux",
      "org.openjfx" % "javafx-graphics" % "21.0.2" classifier "linux",
      "org.openjfx" % "javafx-base" % "21.0.2" classifier "linux",
      "org.openjfx" % "javafx-media" % "21.0.2" classifier "linux",
      "org.openjfx" % "javafx-web" % "21.0.2" classifier "linux"
    ),

    coverageExcludedFiles := ".*Main\\.scala",

    fork := true,

        // Suppress JavaFX and dconf warnings
    javaOptions ++= Seq(
      "-Dprism.verbose=false",
      "-Djavafx.verbose=false",
      "--add-opens", "javafx.graphics/com.sun.javafx.application=ALL-UNNAMED"
    )
  )