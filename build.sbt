ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

val zioHttpVersion = "3.2.0"
val zioVersion = "2.1.17"
val circeVersion = "0.14.12"
val sttpVersion = "4.0.2"

lazy val root = (project in file("."))
  .settings(
    name := "MovieListApp",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client4" %% "core" % sttpVersion,
      "com.softwaremill.sttp.client4" %% "circe" % sttpVersion,

      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,

      "dev.zio" %% "zio" % zioVersion,

      "com.augustnagro" %% "magnum" % "1.3.0",
      "com.zaxxer"    % "HikariCP"    % "5.0.1"   // connection pool
    )
  )
