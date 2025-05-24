package com.shelton.ebu6403.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AnalysisController.
 * This class contains test cases to verify the functionality of transaction analysis
 * including reading from CSV files and data aggregation by different time periods.
 */
public class AnalysisControllerTest {

    private AnalysisController controller;

    /**
     * Sets up the test environment before each test.
     * Initializes a new AnalysisController instance.
     * Note: Path to test data directory needs to be configured here
     */
    @BeforeEach
    public void setUp() {
        controller = new AnalysisController();

        // Configure path to point to test data directory
        // You can also modify path constants in AnalysisController using reflection,
        // or use wrapper functions as alternatives
    }

    /**
     * Tests reading spending transactions from CSV file.
     * Verifies that:
     * - Transactions are successfully read
     * - Each transaction has the correct type (Spend)
     * - Date and amount values are valid
     * - Category is not null
     */
    @Test
    public void testReadTransactionsFromCSV_spend() {
        String path = "JunitTest/test_data/expenses.csv";
        LocalDate date = LocalDate.of(2025, 5, 1);

        List<AnalysisController.Transaction> transactions =
                controller.readTransactionsFromCSV(path, "Spend", date);

        assertNotNull(transactions);
        assertFalse(transactions.isEmpty());

        for (AnalysisController.Transaction t : transactions) {
            assertEquals("Spend", t.getType());
            assertEquals(date.toString(), t.getDate());
            assertTrue(t.getAmount() > 0);
            assertNotNull(t.getCategory());
        }
    }

    /**
     * Tests reading income transactions from CSV file.
     * Verifies that:
     * - Transactions are successfully read
     * - Each transaction has the correct type (Income)
     * - Date and amount values are valid
     * - Category is not null
     */
    @Test
    public void testReadTransactionsFromCSV_income() {
        String path = "JunitTest/test_data/incomes.csv";
        LocalDate date = LocalDate.of(2025, 5, 1);

        List<AnalysisController.Transaction> transactions =
                controller.readTransactionsFromCSV(path, "Income", date);

        assertNotNull(transactions);
        assertFalse(transactions.isEmpty());

        for (AnalysisController.Transaction t : transactions) {
            assertEquals("Income", t.getType());
            assertEquals(date.toString(), t.getDate());
            assertTrue(t.getAmount() > 0);
            assertNotNull(t.getCategory());
        }
    }

    /**
     * Tests daily aggregation of transaction data.
     * Verifies that:
     * - Aggregated data is not null or empty
     * - Days are valid (1-31)
     * - Aggregated amounts are non-negative
     */
    @Test
    public void testAggregateByDayOrMonthOrYear_daily() {
        String path = "JunitTest/test_data/expenses.csv";

        Map<String, Double> dailyData = controller.aggregateByDayOrMonthOrYear(path, "day");
        assertNotNull(dailyData);
        assertFalse(dailyData.isEmpty());

        // Assert that day numbers are valid (1-31)
        for (String dayStr : dailyData.keySet()) {
            int day = Integer.parseInt(dayStr);
            assertTrue(day >= 1 && day <= 31);
            assertTrue(dailyData.get(dayStr) >= 0);
        }
    }

    /**
     * Tests monthly aggregation of transaction data.
     * Verifies that:
     * - Aggregated data is not null or empty
     * - Months are valid (1-12)
     * - Aggregated amounts are non-negative
     */
    @Test
    public void testAggregateByDayOrMonthOrYear_monthly() {
        String path = "JunitTest/test_data/incomes.csv";

        Map<String, Double> monthlyData = controller.aggregateByDayOrMonthOrYear(path, "month");
        assertNotNull(monthlyData);
        assertFalse(monthlyData.isEmpty());

        // Assert that month numbers are valid (1-12)
        for (String monthStr : monthlyData.keySet()) {
            int month = Integer.parseInt(monthStr);
            assertTrue(month >= 1 && month <= 12);
            assertTrue(monthlyData.get(monthStr) >= 0);
        }
    }

    /**
     * Tests yearly aggregation of transaction data.
     * Verifies that:
     * - Aggregated data is not null or empty
     * - Years are valid (2000 or later)
     * - Aggregated amounts are non-negative
     */
    @Test
    public void testAggregateByDayOrMonthOrYear_yearly() {
        String path = "JunitTest/test_data/expenses.csv";

        Map<String, Double> yearlyData = controller.aggregateByDayOrMonthOrYear(path, "year");
        assertNotNull(yearlyData);
        assertFalse(yearlyData.isEmpty());

        // Assert that key is a valid year, typically after 2000
        for (String yearStr : yearlyData.keySet()) {
            int year = Integer.parseInt(yearStr);
            assertTrue(year >= 2000);
            assertTrue(yearlyData.get(yearStr) >= 0);
        }
    }
}
