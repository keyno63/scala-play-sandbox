name := "scala_play"
 
version := "1.0" 
      
lazy val `scala_play` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice ) ++
  Seq(
    "net.ruippeixotog" %% "scala-scraper" % "2.2.0",

    // scalikejdbc
    // https://mvnrepository.com/artifact/org.scalikejdbc/scalikejdbc
    "org.scalikejdbc" %% "scalikejdbc" % "3.4.1",
    "org.scalikejdbc" %% "scalikejdbc-config"           % "3.4.1",
    "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.8.0-scalikejdbc-3.4",
    "org.postgresql" % "postgresql" % "42.2.12"
  ) ++
  Seq(
      "com.typesafe"      %  "config"           % "1.3.1",
      "org.scalikejdbc"   %% "scalikejdbc-test" % "3.4.1"   % "test",
      "org.scalatest"     %% "scalatest"        % "3.0.+"   % "test",
      "org.specs2"        %% "specs2-core"      % "3.8.9"   % "test"
)

//unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )
unmanagedResourceDirectories in Test +=  baseDirectory.value  / "target/web/public/test"
      