import Dependencies._


organization := "com.github.andb0t"
scalaVersion := "2.11.8"
version      := "0.1.0-SNAPSHOT"
name := "mllab"

libraryDependencies += scalaTest % Test

// spark
// val sparkVersion = "2.2.0"
// libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion
// libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion
// libraryDependencies += "com.databricks" %% "spark-csv" % "1.0.3"
// resolvers += "apache-snapshots" at "http://repository.apache.org/snapshots/"

// vegas
// libraryDependencies += "org.vegas-viz" %% "vegas" % "0.3.9"
// libraryDependencies += "org.vegas-viz" %% "vegas-spark" % "0.3.9"
// resolvers += "Mavmav" at "http://mvnrepository.com/artifact/"

// linter
addCompilerPlugin("org.psywerx.hairyfotr" %% "linter" % "0.1.17")
