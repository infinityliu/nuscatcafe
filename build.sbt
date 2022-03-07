name := "nuscatcafe"

version := "1.0"

lazy val `nuscatcafe` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

scalaVersion := "2.12.15"

libraryDependencies ++= Seq(jdbc, ehcache, ws, specs2 % Test, guice,
  // Enable reactive mongo for Play 2.8
  "org.reactivemongo" %% "play2-reactivemongo" % "0.20.13-play28",
  // Provide JSON serialization for reactive mongo
  "org.reactivemongo" %% "reactivemongo-play-json-compat" % "1.0.1-play28",
  // Provide BSON serialization for reactive mongo
  "org.reactivemongo" %% "reactivemongo-bson-compat" % "0.20.13",
  // Provide JSON serialization for Joda-Time
  "com.typesafe.play" %% "play-json-joda" % "2.7.4",
  "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5",
  "com.github.janjaali" %% "spray-jwt" % "1.0.0"
)
