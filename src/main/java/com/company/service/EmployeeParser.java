
package com.company.service;

import com.company.model.Employee;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class EmployeeParser {
    /**
     * Parses a CSV file with header. Expected columns: EmployeeID,Name,ManagerID,Salary
     * ManagerID can be empty/null for CEO.
     */
    public Map<Integer, Employee> parseCsv(Path csvPath) throws IOException {
        Map<Integer, Employee> employees = new HashMap<>();
        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            String line = br.readLine();
            if (line == null) return employees;
            // skip header if present (detect by non-numeric first cell)
            if (line.trim().length() > 0 && !Character.isDigit(line.trim().charAt(0))) {
                line = br.readLine();
            }
            while (line != null) {
                String[] parts = splitCsv(line);
                if (parts.length < 4) { line = br.readLine(); continue; }
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                String mgr = parts[2].trim();
                Integer managerId = (mgr.isEmpty() ? null : Integer.valueOf(mgr));
                double salary = Double.parseDouble(parts[3].trim());
                Employee e = new Employee(id, name, managerId, salary);
                employees.put(id, e);
                line = br.readLine();
            }
        }
        // link subordinates
        for (Employee e : employees.values()) {
            Integer mid = e.getManagerId();
            if (mid != null) {
                Employee m = employees.get(mid);
                if (m != null) m.addSubordinate(e);
            }
        }
        return employees;
    }

    // very simple CSV splitter (no embedded commas handling). Good enough given assumptions.
    private String[] splitCsv(String line) {
        return line.split(",");
    }
}
