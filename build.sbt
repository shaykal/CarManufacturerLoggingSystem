name := "CarManufactureresLoggingSystem"

version := "0.1"

scalaVersion := "2.12.3"

lazy val akkaVersion = "2.5.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  //"org.scalamock" % "scalamock-scalatest-support_2.12" % "3.6.0" % "test"
  "org.mockito" % "mockito-core" % "2.10.0" % "test"

)
