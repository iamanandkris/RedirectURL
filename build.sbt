import play.Project._

name := """hello-play-scala"""

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
   jdbc,
    "com.typesafe.play" %% "play-slick" % "0.3.3" exclude("org.scala-stm", "scala-stm_2.10.0"),
    "org.slf4j" % "slf4j-nop" % "1.6.4" ,
    "org.webjars" %% "webjars-play" % "2.2.0",
    "com.typesafe.slick" %% "slick" % "1.0.1"
)

playScalaSettings
 