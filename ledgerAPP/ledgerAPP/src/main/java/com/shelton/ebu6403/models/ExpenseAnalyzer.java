package com.shelton.ebu6403.models;

import java.util.HashMap;
import java.util.Map;

public class ExpenseAnalyzer {
    private Map<String, Double> categoryExpenses;

    public ExpenseAnalyzer() {
        categoryExpenses = new HashMap<>();
    }

    // 添加支出金额
    public void addExpense(String category, double amount) {
        categoryExpenses.put(category, categoryExpenses.getOrDefault(category, 0.0) + amount);
    }

    // 获取每个品种的消费总额
    public Map<String, Double> getCategoryExpenses() {
        return categoryExpenses;
    }

    // 获取每个品种的年度消费趋势
    public void displayAnnualTrends() {
        System.out.println("Category Annual Spending Trends:");
        for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
            System.out.println("Category: " + entry.getKey() + " | Total: " + entry.getValue());
        }
    }
}
