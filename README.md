# Scala Payroll Analytics

An educational data science project combining an object-oriented payroll model with Apache Spark analysis. The included CSV contains synthetic employee records.

## Run it

Requires Java 17 or newer and sbt. From this project folder, run:

```bash
sbt run
```

To analyze a different CSV file, pass its path:

```bash
sbt "run data/another-payroll.csv"
```

The input CSV needs these columns: `employee_id`, `employee_name`, `department`, `employee_type`, and `gross_pay`. Gross pay must be a positive number; invalid records raise `InvalidPayDataException`.

## What the analysis shows

- Total and average payroll by employee type
- Employee count and highest pay by employee type
- Total and average payroll by department
- The five highest-paid employees
- Overall payroll total

Spark reads the CSV into a DataFrame and performs grouped aggregations and sorting. The sample amounts are INR and represent one pay period.

## Object-oriented model

- Abstract `Employee` defines shared identity and the required `calculatePay()` method.
- `SalariedEmployee`, `HourlyEmployee`, and `ContractEmployee` override the pay calculation.
- `PaySlip.forEmployee()` works with the base employee type and demonstrates polymorphism.
- `InvalidPayDataException` reports blank identity fields and invalid pay amounts.
- `BigDecimal` is used in the pay model for money calculations.

## Presentation outline

1. **Question:** How does the payroll total differ across employee types and departments?
2. **Data:** The project uses a small synthetic CSV dataset with employee type, department, and gross pay.
3. **Model:** The employee subclasses override their pay calculation, and one pay-slip use case accepts every subtype.
4. **Analysis:** Spark groups and summarizes records to compare payroll totals, averages, and highest earners.
5. **Validation:** Invalid or missing gross pay values raise a clear exception.
6. **Demo:** Run `sbt run` and explain the summary tables.

## Example payroll formulas

| Type | Formula | Example gross pay |
|---|---|---:|
| Salaried | Monthly salary | ₹50,000.00 |
| Hourly | Hourly rate × hours worked | ₹40,000.00 |
| Contract | Contract fee + expenses | ₹33,500.00 |

The sample analytics CSV is illustrative and contains no real employee information.
