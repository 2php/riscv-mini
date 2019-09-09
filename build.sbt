val commonSettings = Seq(
  scalaVersion := "2.12.6",
  crossScalaVersions := Seq("2.11.12", "2.12.4"),
  libraryDependencies ++= Seq(
    "edu.berkeley.cs" %% "chisel-testers2" % "0.1-SNAPSHOT",
    "edu.berkeley.cs" %% "chisel-iotesters" % "1.3-SNAPSHOT",
    "org.scalatest" %% "scalatest" % "3.0.1"
  ),
  resolvers ++= Seq(
    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases")
  )
)

val miniSettings = commonSettings ++ Seq(
  name := "riscv-mini",
  version := "2.0-SNAPSHOT",
  organization := "edu.berkeley.cs")

lazy val lib  = project settings commonSettings
lazy val mini = project in file(".") settings miniSettings dependsOn lib
