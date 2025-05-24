package com.shelton.ebu6403.models;

import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * BudgetSet manages user-defined monthly budgets and tracks corresponding expenses.
 * <p>
 * It supports saving and loading budget data from CSV files, calculating progress,
 * and providing callback notifications on budget changes.
 * </p>
 *
 * @author Haoran Jin, Zuhao Zhang, Haihan Sun
 */
public class BudgetSet {

    /** Stores monthly budgets: category -> (YearMonth -> amount) */
    Map<String, Map<YearMonth, Double>> monthlyBudgets;

    /** Stores expenses by category and date: category -> (LocalDate -> amount spent) */
    private Map<String, Map<LocalDate, Double>> categorySpending;

    /** Stores savings per category (optional, currently unused) */
    private Map<String, Map<LocalDate, Double>> categorySavings;

    /** Path to the budget CSV file */
    private static final String BUDGET_FILE = "data/budgets.csv";

    /** Callback triggered when expenses change */
    private Runnable onExpenseChanged;

    /**
     * Constructs a new BudgetSet and loads any existing budget/expense data.
     */
    public BudgetSet() {
        monthlyBudgets = new HashMap<>();
        categorySpending = new HashMap<>();
        categorySavings = new HashMap<>();
        loadBudgetsFromFile();
        loadExpensesFromFile();
    }

    /**
     * Sets the budget amount for a given category and month.
     *
     * @param category the name of the category
     * @param month the month for which the budget is set
     * @param amount the budgeted amount
     */
    public void setBudget(String category, YearMonth month, double amount) {
        monthlyBudgets.putIfAbsent(category, new HashMap<>());
        monthlyBudgets.get(category).put(month, amount);
        saveBudgetsToFile();
    }

    /**
     * Adds an expense to the specified category on a given date.
     *
     * @param category the category of the expense
     * @param date the date of the expense
     * @param amount the amount spent
     */
    public void addExpense(String category, LocalDate date, double amount) {
        categorySpending.putIfAbsent(category, new HashMap<>());
        categorySpending.get(category).put(date,
                categorySpending.get(category).getOrDefault(date, 0.0) + amount);

        notifyExpenseChanged();
    }

    /**
     * Retrieves all budgets for all categories and months.
     *
     * @return a map of category to month-budget mappings
     */
    public Map<String, Map<YearMonth, Double>> getAllBudgets() {
        return monthlyBudgets;
    }

    /**
     * Retrieves the budget for a specific category.
     *
     * @param category the category name
     * @return a map of YearMonth to budget amount
     */
    public Map<YearMonth, Double> getBudgetsByCategory(String category) {
        return monthlyBudgets.getOrDefault(category, new HashMap<>());
    }

    /**
     * Removes a specific month's budget for a given category.
     *
     * @param category the category name
     * @param month the target month
     * @return true if removed successfully; false otherwise
     */
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

    /**
     * Calculates the percentage of budget spent in a given month and category.
     *
     * @param category the budget category
     * @param ym the month
     * @return a value between 0.0 and 1.0 representing progress
     */
    public double getBudgetProgress(String category, YearMonth ym) {
        double budgetAmount = monthlyBudgets
                .getOrDefault(category, new HashMap<>())
                .getOrDefault(ym, 0.0);

        if (budgetAmount == 0.0) return 0.0;

        double totalSpent = 0.0;
        Map<LocalDate, Double> expensesByDate = categorySpending.getOrDefault(category, new HashMap<>());

        for (Map.Entry<LocalDate, Double> entry : expensesByDate.entrySet()) {
            LocalDate date = entry.getKey();
            if (YearMonth.from(date).equals(ym)) {
                totalSpent += entry.getValue();
            }
        }

        return Math.min(totalSpent / budgetAmount, 1.0);
    }

    /**
     * Loads expense records from a CSV file and accumulates them in memory.
     */
    private void loadExpensesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("data/expenses.csv"))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",", -1);
                if (data.length < 5) continue;

                String category = data[4];
                double amount = Double.parseDouble(data[3]);
                LocalDate date = LocalDate.parse(data[2]);

                categorySpending.putIfAbsent(category, new HashMap<>());
                Map<LocalDate, Double> map = categorySpending.get(category);
                map.put(date, map.getOrDefault(date, 0.0) + amount);
            }
        } catch (IOException e) {
            System.out.println("No previous expense data found.");
        }
    }

    /**
     * Loads budget data from the CSV file and stores it in memory.
     */
    private void loadBudgetsFromFile() {
        monthlyBudgets.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(BUDGET_FILE))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
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

    /**
     * Converts a month name (e.g., "March") into a numeric index (0-based).
     *
     * @param month the full English month name
     * @return an integer from 0 (January) to 11 (December)
     */
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

    /**
     * Saves all current budget data to the CSV file.
     */
    private void saveBudgetsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BUDGET_FILE))) {
            writer.write("No,Category,Month,Amount");
            writer.newLine();

            AtomicInteger serial = new AtomicInteger(1);
            monthlyBudgets.entrySet().stream()
                    .flatMap(entry -> entry.getValue().entrySet().stream()
                            .map(e -> new BudgetEntry(entry.getKey(), e.getKey(), e.getValue())))
                    .sorted((a, b) -> {
                        int cmp = a.month.compareTo(b.month);
                        return (cmp == 0) ? a.category.compareTo(b.category) : cmp;
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

    /**
     * A helper data class used for sorting and saving budget entries.
     */
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

    /**
     * Registers a callback to be executed whenever expense data changes.
     *
     * @param callback the function to run on expense updates
     */
    public void setOnExpenseChanged(Runnable callback) {
        this.onExpenseChanged = callback;
    }

    /**
     * Executes the registered expense-changed callback, if any.
     */
    private void notifyExpenseChanged() {
        if (onExpenseChanged != null) onExpenseChanged.run();
    }
}
