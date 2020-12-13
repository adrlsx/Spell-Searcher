name := "Spell-Searcher"

version := "0.1"

scalaVersion := "2.12.12"

libraryDependencies += "org.apache.spark" %% "spark-core" % "3.0.1"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.0.1"
// https://mvnrepository.com/artifact/org.scala-lang.modules/scala-swing
libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"
// https://mvnrepository.com/artifact/com.formdev/flatlaf
// https://alvinalexander.com/scala/how-use-maven-repository-library-with-scala-sbt/
libraryDependencies += "com.formdev" % "flatlaf" % "0.45"