package com.shelton.ebu6403.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Controller class for the home dashboard interface.
 * <p>
 * Manages the main dashboard view showing daily expense statistics,
 * financial charts, bank card information, and recent transactions.
 * </p>
 * @author Haoran Jin, Zhifei liu， Weicheng Xie
 */
public class HomeController {
    /** Labels for category amounts */
    @FXML private Label foodAmount;
    @FXML private Label transportAmount;
    @FXML private Label medicineAmount;
    @FXML private Label giftsAmount;
    @FXML private Label clothingAmount;
    @FXML private Label sportsAmount;

    /** Labels for last update timestamps */
    @FXML private Label foodUpdate;
    @FXML private Label transportUpdate;
    @FXML private Label sportsUpdate;
    @FXML private Label clothingUpdate;

    /** Chart components */
    @FXML private BarChart<String, Number> weeklyChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private PieChart expenseChart;

    /** Bank card information labels */
    @FXML private Label card1Balance;
    @FXML private Label card1Holder;
    @FXML private Label card2Balance;
    @FXML private Label card2Holder;

    /** List view for recent transactions */
    @FXML private ListView<String> recentTransactions;

    /**
     * Initializes the controller.
     * Sets up all dashboard components including statistics, charts, and transaction list.
     */
    @FXML
    public void initialize() {
        initDailyStats();
        configureWeeklyChart();
        initExpenseChart();
        loadTransactions();
        loadBankCards();
    }

    /**
     * Initializes daily statistics.
     * Loads and displays current day's expense amounts for different categories.
     */
    private void initDailyStats() {
        Map<String, Double> amountMap = new HashMap<>();
        LocalDate latestDate = null;

        try (BufferedReader reader = new BufferedReader(new FileReader("data/expenses.csv"))) {
            String line;
            boolean skip = true;
            Map<LocalDate, List<String[]>> allByDate = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                if (skip) { skip = false; continue; }
                String[] parts = line.split(",", -1);
                LocalDate date = LocalDate.parse(parts[2]);
                allByDate.computeIfAbsent(date, d -> new ArrayList<>()).add(parts);
            }

            LocalDate today = LocalDate.now();
            if (allByDate.containsKey(today)) {
                for (String[] row : allByDate.get(today)) {
                    String category = row[4];
                    double amount = Double.parseDouble(row[3]);
                    amountMap.put(category, amountMap.getOrDefault(category, 0.0) + amount);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update amount displays with formatted values
        foodAmount.setText("$" + String.format("%.2f", amountMap.getOrDefault("Food", 0.0)));
        transportAmount.setText("$" + String.format("%.2f", amountMap.getOrDefault("Transportation", 0.0)));
        sportsAmount.setText("$" + String.format("%.2f", amountMap.getOrDefault("Sports", 0.0)));
        clothingAmount.setText("$" + String.format("%.2f", amountMap.getOrDefault("Clothing", 0.0)));

        // Update timestamps with current time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - MMM dd", Locale.ENGLISH);
        String now = "Updated at " + LocalDateTime.now().format(formatter);

        foodUpdate.setText(now);
        transportUpdate.setText(now);
        sportsUpdate.setText(now);
        clothingUpdate.setText(now);
    }

    /**
     * Configures the weekly financial activity chart.
     * Sets up and populates a bar chart showing income and expenses for the past 7 days.
     */
    private void configureWeeklyChart() {
        // Configure Y-axis to show currency values
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis, "$", null));

        // Create series for expenses and income
        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expense");

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");

        // Generate dates for the last 7 days
        LocalDate today = LocalDate.now();
        List<LocalDate> last7Days = IntStream.rangeClosed(1, 7)
                .mapToObj(i -> today.minusDays(7 - i))
                .collect(Collectors.toList());

        // Load financial data
        Map<LocalDate, Double> incomeMap = loadDailyTotal("data/incomes.csv");
        Map<LocalDate, Double> expenseMap = loadDailyTotal("data/expenses.csv");

        // Populate chart data
        for (LocalDate date : last7Days) {
            String label = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            incomeSeries.getData().add(new XYChart.Data<>(label, incomeMap.getOrDefault(date, 0.0)));
            expenseSeries.getData().add(new XYChart.Data<>(label, expenseMap.getOrDefault(date, 0.0)));
        }

        weeklyChart.getData().addAll(incomeSeries, expenseSeries);
        weeklyChart.setStyle("-fx-bar-fill-0: #e74c3c; -fx-bar-fill-1: #2ecc71;");
    }

    /**
     * Loads and aggregates daily financial totals from a CSV file.
     * @param path The path to the CSV file containing financial records
     * @return A map of dates to total amounts for each day
     */
    public Map<LocalDate, Double> loadDailyTotal(String path) {
        Map<LocalDate, Double> result = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            boolean skip = true;
            while ((line = reader.readLine()) != null) {
                if (skip) { skip = false; continue; }
                String[] parts = line.split(",", -1);
                if (parts.length < 4) continue;
                try {
                    LocalDate date = LocalDate.parse(parts[2]);
                    double amount = Double.parseDouble(parts[3]);
                    result.put(date, result.getOrDefault(date, 0.0) + amount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Initializes and populates the expense distribution pie chart.
     * Shows category-wise breakdown of expenses for the current day.
     */
    private void initExpenseChart() {
        Map<String, Double> categoryTotals = new HashMap<>();
        LocalDate today = LocalDate.now();

        try (BufferedReader reader = new BufferedReader(new FileReader("data/expenses.csv"))) {
            String line;
            boolean skip = true;

            while ((line = reader.readLine()) != null) {
                if (skip) { skip = false; continue; }
                String[] parts = line.split(",", -1);
                LocalDate date = LocalDate.parse(parts[2]);
                if (!date.equals(today)) continue;

                String category = parts[4];
                double amount = Double.parseDouble(parts[3]);
                categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        if (pieData.isEmpty()) {
            pieData.add(new PieChart.Data("No Data", 1));
        }

        expenseChart.setData(pieData);
    }

    /**
     * Loads and displays recent transactions in the list view.
     * Combines and sorts transactions from both income and expense files.
     */
    private void loadTransactions() {
        List<Transaction> recentList = new ArrayList<>();

        recentList.addAll(readTransactionsFromCSV("data/expenses.csv"));
        recentList.addAll(readTransactionsFromCSV("data/incomes.csv"));

        // Sort by date in descending order
        recentList.sort((a, b) -> b.getDate().compareTo(a.getDate()));

        ObservableList<String> displayItems = FXCollections.observableArrayList();

        // Display the most recent 10 transactions
        for (int i = 0; i < Math.min(10, recentList.size()); i++) {
            Transaction tx = recentList.get(i);
            String sign = tx.getCategory().equalsIgnoreCase("Salary") ||
                         tx.getCategory().equalsIgnoreCase("Investment") ? "+" : "-";
            displayItems.add(String.format("%s %s$%.2f", tx.getName(), sign, tx.getAmount()));
        }

        recentTransactions.setItems(displayItems);
    }

    /**
     * Model class representing a financial transaction.
     */
    public static class Transaction {
        private final String name;
        private final String date;
        private final double amount;
        private final String category;

        /**
         * Creates a new Transaction instance.
         * @param name The transaction name or description
         * @param date The transaction date in yyyy-MM-dd format
         * @param amount The monetary amount of the transaction
         * @param category The category classification of the transaction
         */
        public Transaction(String name, String date, double amount, String category) {
            this.name = name;
            this.date = date;
            this.amount = amount;
            this.category = category;
        }

        /**
         * Gets the transaction name.
         * @return The transaction name or description
         */
        public String getName() { return name; }

        /**
         * Gets the transaction date.
         * @return The date in yyyy-MM-dd format
         */
        public String getDate() { return date; }

        /**
         * Gets the transaction amount.
         * @return The monetary amount
         */
        public double getAmount() { return amount; }

        /**
         * Gets the transaction category.
         * @return The category name
         */
        public String getCategory() { return category; }
    }

    /**
     * Reads transaction records from a CSV file.
     * Parses each line into a Transaction object.
     * @param path The path to the CSV file
     * @return A list of Transaction objects
     */
    public List<Transaction> readTransactionsFromCSV(String path) {
        List<Transaction> result = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) return result;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst) { isFirst = false; continue; }
                String[] parts = line.split(",", -1);
                if (parts.length == 5) {
                    result.add(new Transaction(
                        parts[1],  // name
                        parts[2],  // date
                        Double.parseDouble(parts[3]),  // amount
                        parts[4]   // category
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Loads and displays bank card information.
     * Updates UI labels with sample card balances and holder names.
     * Note: This method currently uses static sample data for demonstration purposes.
     */
    private void loadBankCards() {
        card1Balance.setText("$5,756.38");
        card1Holder.setText("Eddy Cusuma");
        card2Balance.setText("$2,345.67");
        card2Holder.setText("Shelton Li");
    }
}

