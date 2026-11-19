package payroll

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object PayrollAnalytics {
  def main(args: Array[String]): Unit = {
    val inputPath = args.headOption.getOrElse("data/payroll.csv")
    val spark = SparkSession.builder()
      .appName("Scala Payroll Analytics")
      .master("local[*]")
      .getOrCreate()

    try {
      val payroll = spark.read
        .option("header", "true")
        .option("inferSchema", "true")
        .csv(inputPath)

      val requiredColumns = Set(
        "employee_id", "employee_name", "department", "employee_type", "gross_pay"
      )
      val missingColumns = requiredColumns -- payroll.columns.toSet
      if (missingColumns.nonEmpty) {
        throw new IllegalArgumentException(
          s"CSV is missing required columns: ${missingColumns.toSeq.sorted.mkString(", ")}"
        )
      }

      val invalidPayCount = payroll
        .filter(col("gross_pay").isNull || col("gross_pay") <= 0)
        .count()
      if (invalidPayCount > 0) {
        throw InvalidPayDataException(
          s"Found ${invalidPayCount} record(s) with missing, zero, or negative gross pay."
        )
      }

      println("PAYROLL DATA PREVIEW")
      payroll.show(8, truncate = false)

      println("PAYROLL SUMMARY BY EMPLOYEE TYPE")
      payroll.groupBy("employee_type")
        .agg(
          count("employee_id").as("employee_count"),
          round(sum("gross_pay"), 2).as("total_payroll_inr"),
          round(avg("gross_pay"), 2).as("average_pay_inr"),
          round(max("gross_pay"), 2).as("highest_pay_inr")
        )
        .orderBy(desc("total_payroll_inr"))
        .show(truncate = false)

      println("PAYROLL SUMMARY BY DEPARTMENT")
      payroll.groupBy("department")
        .agg(
          count("employee_id").as("employee_count"),
          round(sum("gross_pay"), 2).as("total_payroll_inr"),
          round(avg("gross_pay"), 2).as("average_pay_inr")
        )
        .orderBy(desc("total_payroll_inr"))
        .show(truncate = false)

      println("TOP 5 EMPLOYEES BY GROSS PAY")
      payroll.select("employee_id", "employee_name", "department", "employee_type", "gross_pay")
        .orderBy(desc("gross_pay"))
        .limit(5)
        .show(truncate = false)

      val totalPayroll = payroll.agg(round(sum("gross_pay"), 2).as("total_payroll_inr"))
      println("TOTAL PAYROLL")
      totalPayroll.show(truncate = false)
    } finally {
      spark.stop()
    }
  }
}
