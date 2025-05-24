package com.shelton.ebu6403.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AnalysisControllerTest {

    private AnalysisController controller;

    @BeforeEach
    public void setUp() {
        controller = new AnalysisController();

        // 这里修改路径指向测试数据目录
        // 如果需要，你也可以反射修改AnalysisController中的路径常量，或者用包装函数替代
    }

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

    @Test
    public void testAggregateByDayOrMonthOrYear_daily() {
        String path = "JunitTest/test_data/expenses.csv";

        Map<String, Double> dailyData = controller.aggregateByDayOrMonthOrYear(path, "day");
        assertNotNull(dailyData);
        assertFalse(dailyData.isEmpty());

        // 断言key是合法的日期号
        for (String dayStr : dailyData.keySet()) {
            int day = Integer.parseInt(dayStr);
            assertTrue(day >= 1 && day <= 31);
            assertTrue(dailyData.get(dayStr) >= 0);
        }
    }

    @Test
    public void testAggregateByDayOrMonthOrYear_monthly() {
        String path = "JunitTest/test_data/incomes.csv";

        Map<String, Double> monthlyData = controller.aggregateByDayOrMonthOrYear(path, "month");
        assertNotNull(monthlyData);
        assertFalse(monthlyData.isEmpty());

        // 断言key是月份号
        for (String monthStr : monthlyData.keySet()) {
            int month = Integer.parseInt(monthStr);
            assertTrue(month >= 1 && month <= 12);
            assertTrue(monthlyData.get(monthStr) >= 0);
        }
    }

    @Test
    public void testAggregateByDayOrMonthOrYear_yearly() {
        String path = "JunitTest/test_data/expenses.csv";

        Map<String, Double> yearlyData = controller.aggregateByDayOrMonthOrYear(path, "year");
        assertNotNull(yearlyData);
        assertFalse(yearlyData.isEmpty());

        // 断言key是年份，通常2020以后
        for (String yearStr : yearlyData.keySet()) {
            int year = Integer.parseInt(yearStr);
            assertTrue(year >= 2000);
            assertTrue(yearlyData.get(yearStr) >= 0);
        }
    }

}
