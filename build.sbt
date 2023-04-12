ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "movie-reservations",
    version := "1.0",
    libraryDependencies ++= library
  )

val akkaHttpVersion = "10.5.0"
val akkaVersion = "2.8.0"
val slickVersion = "3.4.1"
val postgresVersion = "42.5.4"
val flywayVersion = "9.16.0"
val macwireVersion = "2.5.8"
val logbackVersion = "1.4.6"
val sprayJsonVersion = "1.3.6"
val slf4jVersion = "2.0.5"
val scalaTestVersion = "3.2.15"
val akkaTestkitVersion = "2.8.0"
val circeVersion = "0.14.3"
val akkaHttpCirceVersion = "1.39.2"
val catsVersion = "2.9.0"

lazy val library = Seq(
  "com.typesafe.akka"          %% "akka-actor"           % akkaVersion,
  "com.typesafe.akka"          %% "akka-stream"          % akkaVersion,
  "com.typesafe.akka"          %% "akka-actor-typed"     % akkaVersion,
  "com.typesafe.akka"          %% "akka-http"            % akkaHttpVersion,
  "com.typesafe.akka"          %% "akka-http-core"       % akkaHttpVersion,
  "com.typesafe.akka"          %% "akka-http-spray-json" % akkaHttpVersion,
  "de.heikoseeberger"          %% "akka-http-circe"      % akkaHttpCirceVersion,
  "io.spray"                   %% "spray-json"           % "1.3.6",
  "org.postgresql"             % "postgresql"            % postgresVersion,
  "com.softwaremill.macwire"   %% "macros"               % macwireVersion % "provided",
  "com.softwaremill.macwire"   %% "macrosakka"           % macwireVersion % "provided",
  "com.softwaremill.macwire"   %% "util"                 % macwireVersion,
  "com.softwaremill.macwire"   %% "proxy"                % macwireVersion,
  "io.circe"                   %% "circe-core"           % circeVersion,
  "io.circe"                   %% "circe-generic"        % circeVersion,
  "io.circe"                   %% "circe-jawn"           % circeVersion,
  "io.circe"                   %% "circe-generic-extras" % circeVersion,
  "org.flywaydb"               % "flyway-core"           % flywayVersion,
  "com.typesafe.slick"         %% "slick"                % slickVersion,
  "com.typesafe.slick"         %% "slick-hikaricp"       % slickVersion,
  "ch.qos.logback"             % "logback-classic"       % logbackVersion,
  "org.slf4j"                  % "slf4j-api"             % slf4jVersion,
  "org.scalatest"              %% "scalatest"            % scalaTestVersion % Test,
  "com.typesafe.akka"          %% "akka-testkit"         % akkaTestkitVersion % Test,
  "com.typesafe.scala-logging" %% "scala-logging"        % "3.9.5",
  "com.typesafe"               % "config"                % "1.4.2",
  "org.mockito"                %% "mockito-scala"        % "1.17.12" % Test,
  "org.specs2"                 %% "specs2-core"          % "4.19.2" % Test,
  "org.typelevel"              %% "cats-core"            % catsVersion
)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-encoding",
  "utf8"
)

enablePlugins(FlywayPlugin)
flywayUrl := "jdbc:postgresql://" + sys.env.getOrElse("PG_HOST", "localhost") + ":5432/" + sys.env.getOrElse("PG_DB", "cinema")
flywayUser := sys.env.getOrElse("PG_USER", "postgres")
flywayPassword := sys.env.getOrElse("PG_PASSWORD", "admin")
flywayLocations := Seq("filesystem:src/main/resources/db/migration")
