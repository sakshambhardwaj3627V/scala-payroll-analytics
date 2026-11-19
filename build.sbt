ThisBuild / scalaVersion := "2.13.18"
ThisBuild / version := "1.0.0"
ThisBuild / name := "scala-payroll-analytics"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "4.3.0"

Compile / run / mainClass := Some("payroll.PayrollAnalytics")
