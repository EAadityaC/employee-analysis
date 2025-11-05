=== src/main/java/com/company/model/Employee.java ===
package com.company.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Employee {
    private final int id;
    private final String name;
    private final Integer managerId; // nullable
    private final double salary;
    private final List<Employee> subordinates = new ArrayList<>();

    public Employee(int id, String name, Integer managerId, double salary) {
        this.id = id;
        this.name = name;
        this.managerId = managerId;
        this.salary = salary;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Integer getManagerId() { return managerId; }
    public double getSalary() { return salary; }
    public List<Employee> getSubordinates() { return subordinates; }

    public void addSubordinate(Employee e) { subordinates.add(e); }

    @Override
    public String toString() {
        return String.format("%s (ID %d)", name, id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return id == employee.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
