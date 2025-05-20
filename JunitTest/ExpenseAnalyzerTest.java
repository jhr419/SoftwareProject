import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class ExpenseAnalyzerTest {

    private ExpenseAnalyzer analyzer;

    @Before
    public void setUp() {
        analyzer = new ExpenseAnalyzer();
    }

    @After
    public void tearDown() {
        analyzer = null;
    }

    @Test
    public void addExpense() {
        analyzer.addExpense("Food", 100.0);
        analyzer.addExpense("Food", 50.0);
        Map<String, Double> expenses = analyzer.getCategoryExpenses();
        assertTrue(expenses.containsKey("Food"));
        assertEquals(150.0, expenses.get("Food"), 0.001);
    }

    @Test
    public void getCategoryExpenses() {
        analyzer.addExpense("Travel", 200.0);
        analyzer.addExpense("Books", 80.0);

        Map<String, Double> expenses = analyzer.getCategoryExpenses();
        assertEquals(2, expenses.size());
        assertEquals(200.0, expenses.get("Travel"), 0.001);
        assertEquals(80.0, expenses.get("Books"), 0.001);
    }

    @Test
    public void displayAnnualTrends() {
        analyzer.addExpense("Shopping", 300.0);
        analyzer.addExpense("Utilities", 120.5);

        // 输出测试通常不使用断言，这里只是为了覆盖调用路径，验证不会抛出异常
        try {
            analyzer.displayAnnualTrends();
        } catch (Exception e) {
            fail("displayAnnualTrends() should not throw an exception");
        }
    }
}
