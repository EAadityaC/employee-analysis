package com.company;

import com.company.model.Employee;
import com.company.service.EmployeeAnalyzer;
import com.company.service.EmployeeParser;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class App {
    private static final int MAX_MANAGERS_ALLOWED = 4;

    public static void main(String[] args) throws Exception {
        Path csvPath;
        if (args.length >= 1) {
            csvPath = Paths.get(args[0]);
        } else {
            // default to uploaded path (as provided in the conversation environment)
            csvPath = Paths.get("/mnt/data/employee.csv");
            System.out.println("No CSV path provided. Using default: /mnt/data/employee.csv");
        }

        EmployeeParser parser = new EmployeeParser();
        Map<Integer, Employee> employees = parser.parseCsv(csvPath);
        if (employees.isEmpty()) {
            System.out.println("No employee data found. Exiting.");
            return;
        }

        EmployeeAnalyzer analyzer = new EmployeeAnalyzer(employees);

        List<EmployeeAnalyzer.ManagerDeviation> tooLittle = analyzer.managersEarningTooLittle();
        List<EmployeeAnalyzer.ManagerDeviation> tooMuch = analyzer.managersEarningTooMuch();
        Map<Employee, Integer> tooLongLines = analyzer.employeesWithTooLongReportingLine(MAX_MANAGERS_ALLOWED);

        System.out.println("\nManagers earning less than required:");
        if (tooLittle.isEmpty()) System.out.println("  (none)");
        else {
            for (EmployeeAnalyzer.ManagerDeviation d : tooLittle) {
                double shortBy = -d.difference; // stored negative
                System.out.printf("  %s earns %.2f less than the required minimum of %.2f (actual %.2f).%n",
                        d.manager, shortBy, d.expectedSalaryLowerBound, d.actualSalary);
            }
        }

        System.out.println("\nManagers earning more than allowed:");
        if (tooMuch.isEmpty()) System.out.println("  (none)");
        else {
            for (EmployeeAnalyzer.ManagerDeviation d : tooMuch) {
                System.out.printf("  %s earns %.2f more than the allowed maximum of %.2f (actual %.2f).%n",
                        d.manager, d.difference, d.expectedSalaryUpperBound, d.actualSalary);
            }
        }

        System.out.println("\nEmployees with a reporting line longer than " + MAX_MANAGERS_ALLOWED + " managers:");
        if (tooLongLines.isEmpty()) System.out.println("  (none)");
        else {
            for (Map.Entry<Employee, Integer> e : tooLongLines.entrySet()) {
                int actualManagers = analyzer.countManagersToCeo(e.getKey());
                System.out.printf("  %s has %d managers between them and the CEO (exceeds by %d).%n",
                        e.getKey(), actualManagers, e.getValue());
            }
        }

        System.out.println("\nDone.");
    }
}
