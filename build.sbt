name := "scala_play"
 
version := "1.0" 
      
lazy val `scala_play` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"
val playVersion = "2.8.0"
val scalikejdbcVersion = "3.4.2"
val scalikejdbcInitializerVersion = "3.4"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice ) ++
  Seq(
    "net.ruippeixotog" %% "scala-scraper" % "2.2.0",

    // scalikejdbc
    // https://mvnrepository.com/artifact/org.scalikejdbc/scalikejdbc
    "org.scalikejdbc" %% "scalikejdbc" % scalikejdbcVersion,
    "org.scalikejdbc" %% "scalikejdbc-config"           % scalikejdbcVersion,
    "org.scalikejdbc" %% "scalikejdbc-play-initializer" % s"${playVersion}-scalikejdbc-${scalikejdbcInitializerVersion}",
    "org.postgresql" % "postgresql" % "42.2.12"
  ) ++
  Seq(
      "com.typesafe"      %  "config"           % "1.3.1",
      "org.scalikejdbc"   %% "scalikejdbc-test" % scalikejdbcVersion   % "test",
      "org.scalatest"     %% "scalatest"        % "3.0.+"              % "test",
      "org.specs2"        %% "specs2-core"      % "3.8.9"              % "test"
)

//unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )
unmanagedResourceDirectories in Test +=  baseDirectory.value  / "target/web/public/test"
      