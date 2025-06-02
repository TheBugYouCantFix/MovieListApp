ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.7.0"

val zioHttpVersion = "3.2.0"
val zioVersion = "2.1.17"
val sttpVersion = "4.0.7"
val tapirVersion = "1.11.29"
val ironVersion = "3.0.0"

lazy val root = (project in file("."))
  .settings(
    name := "MovieListApp",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client4" %% "core" % sttpVersion,
      "com.softwaremill.sttp.client4" %% "circe" % sttpVersion,

      "dev.zio" %% "zio" % zioVersion,

      "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-netty-server-zio" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-iron" % tapirVersion,

      "com.augustnagro" %% "magnumzio" % "2.0.0-M1",
      "com.zaxxer"    % "HikariCP"    % "5.0.1", // connection pool
      "org.postgresql" % "postgresql" % "42.7.3",

      "io.github.iltotore" %% "iron" % ironVersion,
      "io.github.iltotore" %% "iron-circe" % ironVersion,

      "com.github.jwt-scala" %% "jwt-circe" % "10.0.4"
    )
  )
