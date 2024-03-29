name := "gitswitch"

version := "1.0"

lazy val `gitswitch` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  filters,
  "mysql" % "mysql-connector-java" % "5.1.41",
  "com.typesafe.play" %% "play-slick" % "2.1.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.1.0",
  "com.nimbusds" % "nimbus-jose-jwt" % "4.34.2",
  cache,
  ws,
  specs2 % Test
)

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
