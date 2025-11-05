package com.company;

import com.company.model.Employee;
import com.company.service.EmployeeAnalyzer;
import com.company.service.EmployeeParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class EmployeeAnalyzerTest {

    /**
     * Create a small CSV in a temp file and run analyzer tests against it.
     */
    @Test
    public void testManagerDeviationsAndLongLines() throws IOException {
        Path tmp = Files.createTempFile("employees", ".csv");
        try (BufferedWriter bw = Files.newBufferedWriter(tmp)) {
            bw.write("EmployeeID,Name,Salary,ManagerID\n");
   /*       bw.write("1,CEO,200000,,\n");
            bw.write("2,John,1,120000\n");
            bw.write("3,Mary,1,210000\n");
            bw.write("4,Steve,2,80000\n");
            bw.write("5,Anne,2,85000\n");
            bw.write("6,Bob,3,70000\n");
            // create deeper chain: 7->6->3->1 (3 managers), add more to exceed 4
            bw.write("7,Alice,6,60000\n");
            bw.write("8,Sam,7,50000\n");
            bw.write("9,Kim,8,40000\n");
            bw.write("10,Jake,9,30000\n");
*/
        bw.write("	123,Joe,Doe,60000, 	\n");
bw.write("	124,Martin,Chekov,45000,123 	\n");
bw.write("	125,Bob,Ronstad,47000,123 	\n");
bw.write("	300,Alice,Hasacat,50000,124 	\n");
bw.write("	305,Brett,Hardleaf,34000,300	\n");
bw.write("	210,Maria James, 44000, 213	\n");
bw.write("	211, Alex Montogemy, 46000, 123	\n");
bw.write("	212, Gopal Shruthi,49000, 311	\n");
bw.write("	213, Kiran Motwani, 51000, 511	\n");
bw.write("	214, Kiron Kher, 33000, 413	\n");
bw.write("	215, Deepak Sagar, 30000, 213	\n");
bw.write("	310, Ruchi Srivastava, 40000, 124	\n");
bw.write("	311, Trideev Roy, 35000, 211	\n");
bw.write("	312, Umesh James, 55000,  500	\n");
bw.write("	313, Shailesh Sagar, 53000, 123	\n");
bw.write("	314, Mansi Pindwar, 33500, 211	\n");
bw.write("	315, Tomas Chavosky, 51000, 123	\n");
bw.write("	316, Phuong Jesi, 33000, 212	\n");
bw.write("	411, Kamal Hasan, 44000, 300	\n");
bw.write("	412, Rajnikanth, 55000, 123	\n");
bw.write("	413, Kate Oconer, 40000, 511	\n");
bw.write("	414, Kiran Jain, 33000, 311	\n");
bw.write("	415, Eugena Russell,35000, 315	\n");
bw.write("	500, Michelle Tessier, 49000, 123	\n");
bw.write("	511, Nicolas Lallier, 44000, 300	\n");
bw.write("	512, Michee Lella, 41000, 312	\n");

        
        }

        EmployeeParser parser = new EmployeeParser();
        Map<Integer, Employee> employees = parser.parseCsv(tmp);
        EmployeeAnalyzer analyzer = new EmployeeAnalyzer(employees);

        List<EmployeeAnalyzer.ManagerDeviation> tooLittle = analyzer.managersEarningTooLittle();
        List<EmployeeAnalyzer.ManagerDeviation> tooMuch = analyzer.managersEarningTooMuch();
        Map<Employee, Integer> tooLongLines = analyzer.employeesWithTooLongReportingLine(4);

        // Mary manages Bob (70k) and Alice indirectly? Mary's direct subordinates only include Bob (70k)
        // John's subordinates Steve(80k) & Anne(85k) avg=82.5 => lower bound=99k => John=120k ok
        // Mary salary 210k > Bob avg 70k *1.5 =105k => Mary is too high
        Assertions.assertTrue(tooMuch.stream().anyMatch(d -> d.manager.getName().equals("Mary")));
        Assertions.assertTrue(tooLittle.stream().noneMatch(d -> d.manager.getName().equals("John")));

        // employee 10 should have long chain: managers are 9,8,7,6,3,1 => count 6 (>4)
        Assertions.assertTrue(tooLongLines.keySet().stream().anyMatch(e -> e.getName().equals("Jake")));
    }
}
