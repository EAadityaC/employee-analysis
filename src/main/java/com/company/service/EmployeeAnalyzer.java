package com.company.service;

import com.company.model.Employee;

import java.util.*;
import java.util.stream.Collectors;

public class EmployeeAnalyzer {
    private final Map<Integer, Employee> employees;

    public EmployeeAnalyzer(Map<Integer, Employee> employees) {
        this.employees = Objects.requireNonNull(employees);
    }

    public static class ManagerDeviation {
        public final Employee manager;
        public final double expectedSalaryLowerBound; // 20% above avg
        public final double expectedSalaryUpperBound; // 50% above avg
        public final double actualSalary;
        public final double difference; // positive if actual > bound (for upper), negative if less than lower bound

        public ManagerDeviation(Employee manager, double expectedLower, double expectedUpper, double actual, double diff) {
            this.manager = manager;
            this.expectedSalaryLowerBound = expectedLower;
            this.expectedSalaryUpperBound = expectedUpper;
            this.actualSalary = actual;
            this.difference = diff;
        }
    }

    /**
     * Find managers earning less than 20% above avg of their direct subordinates.
     */
    public List<ManagerDeviation> managersEarningTooLittle() {
        List<ManagerDeviation> out = new ArrayList<>();
        for (Employee m : employees.values()) {
            if (m.getSubordinates().isEmpty()) continue;
            double avg = m.getSubordinates().stream().mapToDouble(Employee::getSalary).average().orElse(0);
            double lower = avg * 1.20; // 20% more than average
            if (m.getSalary() < lower) {
                double diff = lower - m.getSalary();
                out.add(new ManagerDeviation(m, lower, avg * 1.50, m.getSalary(), -diff));
            }
        }
        return out;
    }

    /**
     * Find managers earning more than 50% above avg of their direct subordinates.
     */
    public List<ManagerDeviation> managersEarningTooMuch() {
        List<ManagerDeviation> out = new ArrayList<>();
        for (Employee m : employees.values()) {
            if (m.getSubordinates().isEmpty()) continue;
            double avg = m.getSubordinates().stream().mapToDouble(Employee::getSalary).average().orElse(0);
            double upper = avg * 1.50; // 50% more than average
            if (m.getSalary() > upper) {
                double diff = m.getSalary() - upper;
                out.add(new ManagerDeviation(m, avg * 1.20, upper, m.getSalary(), diff));
            }
        }
        return out;
    }

    /**
     * Count managers between employee and CEO (inclusive of immediate manager up to CEO, excluding employee)
     */
    public int countManagersToCeo(Employee e) {
        int count = 0;
        Integer mid = e.getManagerId();
        Set<Integer> visited = new HashSet<>();
        while (mid != null) {
            if (visited.contains(mid)) break; // cycle protection
            visited.add(mid);
            Employee mgr = employees.get(mid);
            if (mgr == null) break; // broken chain
            count++;
            mid = mgr.getManagerId();
        }
        return count;
    }

    /**
     * Returns map of employee -> excess managers count (count - maxAllowed) for those exceeding maxAllowed
     */
    public Map<Employee, Integer> employeesWithTooLongReportingLine(int maxAllowed) {
        Map<Employee, Integer> out = new LinkedHashMap<>();
        for (Employee e : employees.values()) {
            int count = countManagersToCeo(e);
            if (count > maxAllowed) {
                out.put(e, count - maxAllowed);
            }
        }
        return out;
    }
}
