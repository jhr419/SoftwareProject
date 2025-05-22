package com.shelton.ebu6403.models;

import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class BudgetSet {
    Map<String, Map<YearMonth, Double>> monthlyBudgets; // {类别 -> {截止日期 -> 预算金额}}
    private Map<String, Map<LocalDate, Double>> categorySpending; // {类别 -> {日期 -> 已支出金额}}
    private static final String BUDGET_FILE = "data/budgets.csv";
    private Runnable onExpenseChanged;
    private Map<String, Map<LocalDate, Double>> categorySavings;  // {类别 -> {日期 -> 储蓄金额}}
    public BudgetSet() {
        monthlyBudgets = new HashMap<>();
        categorySpending = new HashMap<>();
        categorySavings = new HashMap<>();
        loadBudgetsFromFile();
        loadExpensesFromFile();
    }

    // **设定预算**
    public void setBudget(String category, YearMonth month, double amount) {
        monthlyBudgets.putIfAbsent(category, new HashMap<>());
        monthlyBudgets.get(category).put(month, amount);
        saveBudgetsToFile();
    }


    // **记录支出**
    public void addExpense(String category, LocalDate date, double amount) {
        categorySpending.putIfAbsent(category, new HashMap<>());
        categorySpending.get(category).put(date,
                categorySpending.get(category).getOrDefault(date, 0.0) + amount);

        notifyExpenseChanged();
    }


    // **查询所有预算**
    public Map<String, Map<YearMonth, Double>> getAllBudgets() {
        return monthlyBudgets;
    }

    // **按类别查询预算**
    public Map<YearMonth, Double> getBudgetsByCategory(String category) {
        return monthlyBudgets.getOrDefault(category, new HashMap<>());
    }


    // **删除预算（优化交互）**
    public boolean removeBudget(String category, YearMonth month) {
        if (monthlyBudgets.containsKey(category) && monthlyBudgets.get(category).containsKey(month)) {
            monthlyBudgets.get(category).remove(month);
            if (monthlyBudgets.get(category).isEmpty()) {
                monthlyBudgets.remove(category);
            }
            saveBudgetsToFile();
            return true;
        }
        return false;
    }

    // **获取预算进度**
    public double getBudgetProgress(String category, YearMonth ym) {
        // 获取预算金额
        double budgetAmount = monthlyBudgets
                .getOrDefault(category, new HashMap<>())
                .getOrDefault(ym, 0.0);

        if (budgetAmount == 0.0) return 0.0;

        // 计算该分类在这个月的支出总额
        double totalSpent = 0.0;
        Map<LocalDate, Double> expensesByDate = categorySpending.getOrDefault(category, new HashMap<>());

        for (Map.Entry<LocalDate, Double> entry : expensesByDate.entrySet()) {
            LocalDate date = entry.getKey();
            if (YearMonth.from(date).equals(ym)) {
                totalSpent += entry.getValue();
            }
        }

        // 返回支出/预算的百分比（最大为1.0）
        return Math.min(totalSpent / budgetAmount, 1.0);
    }
    private void loadExpensesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("data/expenses.csv"))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) { // 跳过表头
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",", -1);
                if (data.length < 5) continue;

                String category = data[4]; // 第5列是 category
                double amount = Double.parseDouble(data[3]); // 第4列是 amount
                LocalDate date = LocalDate.parse(data[2]); // 第3列是 date

                // 存入 categorySpending
                categorySpending.putIfAbsent(category, new HashMap<>());
                Map<LocalDate, Double> map = categorySpending.get(category);
                map.put(date, map.getOrDefault(date, 0.0) + amount);
            }
        } catch (IOException e) {
            System.out.println("No previous expense data found.");
        }
    }



    // **加载预算**
    private void loadBudgetsFromFile() {
        monthlyBudgets.clear(); // 清空已有数据
        try (BufferedReader reader = new BufferedReader(new FileReader(BUDGET_FILE))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {  // 跳过表头
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",", -1);
                if (data.length < 4) continue;

                String category = data[1];
                String[] monthParts = data[2].split(" ");
                if (monthParts.length != 2) continue;

                int month = getMonthIndex(monthParts[0]) + 1;
                int year = Integer.parseInt(monthParts[1]);
                YearMonth ym = YearMonth.of(year, month);

                double amount = Double.parseDouble(data[3]);

                monthlyBudgets.putIfAbsent(category, new HashMap<>());
                monthlyBudgets.get(category).put(ym, amount);
            }
        } catch (IOException e) {
            System.out.println("No previous budget data found.");
        }
    }



    // 用于解析字符串月份为 index
    private int getMonthIndex(String month) {
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        for (int i = 0; i < months.length; i++) {
            if (months[i].equalsIgnoreCase(month)) return i;
        }
        return 0;
    }


    // **保存预算**
    private void saveBudgetsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BUDGET_FILE))) {
            // ✅ 写入表头
            writer.write("No,Category,Month,Amount");
            writer.newLine();

            AtomicInteger serial = new AtomicInteger(1);
            monthlyBudgets.entrySet().stream()
                    .flatMap(entry -> entry.getValue().entrySet().stream()
                            .map(e -> new BudgetEntry(entry.getKey(), e.getKey(), e.getValue())))
                    .sorted((a, b) -> {
                        int cmp = a.month.compareTo(b.month);
                        if (cmp == 0) return a.category.compareTo(b.category);
                        return cmp;
                    })
                    .forEachOrdered(budget -> {
                        try {
                            String monthStr = budget.month.getMonth().name().substring(0, 1).toUpperCase() +
                                    budget.month.getMonth().name().substring(1).toLowerCase() +
                                    " " + budget.month.getYear();
                            writer.write(serial.getAndIncrement() + "," + budget.category + "," + monthStr + "," + budget.amount);
                            writer.newLine();
                        } catch (IOException e) {
                            System.out.println("Error writing line: " + e.getMessage());
                        }
                    });

        } catch (IOException e) {
            System.out.println("Error saving budget data: " + e.getMessage());
        }
    }

    private static class BudgetEntry {
        String category;
        YearMonth month;
        double amount;

        public BudgetEntry(String category, YearMonth month, double amount) {
            this.category = category;
            this.month = month;
            this.amount = amount;
        }
    }
    public void setOnExpenseChanged(Runnable callback) {
        this.onExpenseChanged = callback;
    }

    private void notifyExpenseChanged() {
        if (onExpenseChanged != null) onExpenseChanged.run();
    }




}