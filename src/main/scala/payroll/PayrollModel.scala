package payroll

/** Base class for employee types that calculate their own gross pay. */
abstract class Employee(val employeeId: String, val name: String) {
  if (employeeId.trim.isEmpty) throw InvalidPayDataException("Employee ID cannot be blank.")
  if (name.trim.isEmpty) throw InvalidPayDataException("Employee name cannot be blank.")

  def employeeType: String
  def calculatePay(): BigDecimal
}

final case class InvalidPayDataException(message: String)
    extends IllegalArgumentException(message)

final class SalariedEmployee(id: String, employeeName: String, val monthlySalary: BigDecimal)
    extends Employee(id, employeeName) {
  PayrollValidation.requirePositive(monthlySalary, "Monthly salary")
  override val employeeType: String = "Salaried"
  override def calculatePay(): BigDecimal = monthlySalary
}

final class HourlyEmployee(
    id: String,
    employeeName: String,
    val hourlyRate: BigDecimal,
    val hoursWorked: BigDecimal
) extends Employee(id, employeeName) {
  PayrollValidation.requirePositive(hourlyRate, "Hourly rate")
  PayrollValidation.requireNonNegative(hoursWorked, "Hours worked")
  override val employeeType: String = "Hourly"
  override def calculatePay(): BigDecimal = hourlyRate * hoursWorked
}

final class ContractEmployee(
    id: String,
    employeeName: String,
    val contractFee: BigDecimal,
    val expenses: BigDecimal = BigDecimal(0)
) extends Employee(id, employeeName) {
  PayrollValidation.requirePositive(contractFee, "Contract fee")
  PayrollValidation.requireNonNegative(expenses, "Expenses")
  override val employeeType: String = "Contract"
  override def calculatePay(): BigDecimal = contractFee + expenses
}

private object PayrollValidation {
  def requirePositive(amount: BigDecimal, field: String): Unit =
    if (amount <= 0) throw InvalidPayDataException(s"${field} must be greater than zero.")

  def requireNonNegative(amount: BigDecimal, field: String): Unit =
    if (amount < 0) throw InvalidPayDataException(s"${field} cannot be negative.")
}

final case class PaySlip(
    employeeId: String,
    employeeName: String,
    employeeType: String,
    grossPay: BigDecimal,
    currency: String = "INR"
) {
  def format: String = {
    val amount = grossPay.setScale(2, BigDecimal.RoundingMode.HALF_UP)
    s"""======================================
       |                 PAY SLIP
       |======================================
       |Employee : $employeeName ($employeeId)
       |Type     : $employeeType
       |Gross pay: ${currency.toUpperCase} ${amount.bigDecimal.toPlainString}
       |======================================""".stripMargin
  }
}

object PaySlip {
  def forEmployee(employee: Employee, currency: String = "INR"): PaySlip =
    PaySlip(
      employee.employeeId,
      employee.name,
      employee.employeeType,
      employee.calculatePay(),
      currency
    )
}
