import Dependencies._


organization := "com.github.andb0t"
scalaVersion := "2.11.8"
version      := "0.1.0-SNAPSHOT"
name := "mllab"

target in Compile in doc := baseDirectory.value / "api"
scalacOptions in Compile in doc ++= Seq("-doc-root-content", "rootdoc.txt")

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

// breeze
libraryDependencies += "org.scalanlp" %% "breeze" % "0.13.2"
libraryDependencies += "org.scalanlp" %% "breeze-natives" % "0.13.2"
libraryDependencies += "org.scalanlp" %% "breeze-viz" % "0.13.2"

resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"

// linter
addCompilerPlugin("org.psywerx.hairyfotr" %% "linter" % "0.1.17")
