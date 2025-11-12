# Employee Analysis

This small Java console application reads an `employee.csv` file and reports:

- managers earning less than 20% above their direct subordinates' average salary
- managers earning more than 50% above their direct subordinates' average salary
- employees with more than 4 managers between them and the CEO (reporting line too long)

## Build & Run

1. Build with Maven:

```
mvn clean package
```

2. Run (provide path to CSV or it will try `/employee.csv`):

```
java -jar target/employee-analysis-1.0-SNAPSHOT.jar /path/to/employee.csv
```

## Notes & Assumptions

- CSV expected format: `EmployeeID,Name,Salary,ManagerID` with header.   
- `ManagerID` empty indicates no manager (CEO).
- CSV parsing is simple and does not handle quoted commas. If your CSV contains embedded commas, please say so and I will update the parser.
- Cycles in the reporting chain are protected against by a visited set; broken chains (missing manager rows) stop at the missing link.

