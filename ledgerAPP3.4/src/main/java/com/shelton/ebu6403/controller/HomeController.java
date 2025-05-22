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

public class HomeController {
    // 卡片金额显示
    @FXML
    private Label foodAmount;
    @FXML private Label transportAmount;
    @FXML private Label medicineAmount;
    @FXML private Label giftsAmount;
    @FXML private Label clothingAmount;
    @FXML private Label sportsAmount;
    @FXML private Label foodUpdate;
    @FXML private Label transportUpdate;
    @FXML private Label sportsUpdate;
    @FXML private Label clothingUpdate;

    // 图表控件
    @FXML private BarChart<String, Number> weeklyChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private PieChart expenseChart;

    // 银行卡信息
    @FXML private Label card1Balance;
    @FXML private Label card1Holder;
    @FXML private Label card2Balance;
    @FXML private Label card2Holder;

    @FXML  // 必须添加此注解
    private ListView<String> recentTransactions; // 类型与泛型需匹配

    @FXML
    public void initialize() {
        // 初始化卡片数据
        initDailyStats();

        // 配置周活动图表
        configureWeeklyChart();

        // 初始化支出饼图
        initExpenseChart();

        // 加载交易记录
        loadTransactions();

        // 加载银行卡信息
        loadBankCards();
    }

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

        // 更新金额显示（默认 0）
        foodAmount.setText("$" + String.format("%.2f", amountMap.getOrDefault("Food", 0.0)));
        transportAmount.setText("$" + String.format("%.2f", amountMap.getOrDefault("Transportation", 0.0)));
        sportsAmount.setText("$" + String.format("%.2f", amountMap.getOrDefault("Sports", 0.0)));
        clothingAmount.setText("$" + String.format("%.2f", amountMap.getOrDefault("Clothing", 0.0)));

        // 如果你 FXML 中还定义了时间戳 Label（如 foodUpdate），也可以添加更新时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - MMM dd", Locale.ENGLISH);
        String now = "Updated at " + LocalDateTime.now().format(formatter);

        foodUpdate.setText(now);
        transportUpdate.setText(now);
        sportsUpdate.setText(now);
        clothingUpdate.setText(now);

    }


    private void configureWeeklyChart() {
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis, "$", null));

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expense");

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");

        LocalDate today = LocalDate.now();
        List<LocalDate> last7Days = IntStream.rangeClosed(1, 7)
                .mapToObj(i -> today.minusDays(7 - i))
                .collect(Collectors.toList());

        Map<LocalDate, Double> incomeMap = loadDailyTotal("data/incomes.csv");
        Map<LocalDate, Double> expenseMap = loadDailyTotal("data/expenses.csv");

        for (LocalDate date : last7Days) {
            String label = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            incomeSeries.getData().add(new XYChart.Data<>(label, incomeMap.getOrDefault(date, 0.0)));
            expenseSeries.getData().add(new XYChart.Data<>(label, expenseMap.getOrDefault(date, 0.0)));
        }

        weeklyChart.getData().addAll(incomeSeries, expenseSeries);
        weeklyChart.setStyle("-fx-bar-fill-0: #e74c3c; -fx-bar-fill-1: #2ecc71;");
    }

    private Map<LocalDate, Double> loadDailyTotal(String path) {
        Map<LocalDate, Double> result = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            boolean skip = true;
            while ((line = reader.readLine()) != null) {
                if (skip) { skip = false; continue; }
                String[] parts = line.split(",", -1);
                if (parts.length < 4) continue; // 跳过非法行
                try {
                    LocalDate date = LocalDate.parse(parts[2]);
                    double amount = Double.parseDouble(parts[3]);
                    result.put(date, result.getOrDefault(date, 0.0) + amount);
                } catch (Exception e) {
                    e.printStackTrace(); // 或忽略解析失败
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


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

    private void loadTransactions() {
        List<Transaction> recentList = new ArrayList<>();

        recentList.addAll(readTransactionsFromCSV("data/expenses.csv"));
        recentList.addAll(readTransactionsFromCSV("data/incomes.csv"));

        // 根据日期降序排列（假设格式为 yyyy-MM-dd）
        recentList.sort((a, b) -> b.getDate().compareTo(a.getDate()));

        ObservableList<String> displayItems = FXCollections.observableArrayList();

        for (int i = 0; i < Math.min(10, recentList.size()); i++) {
            Transaction tx = recentList.get(i);
            String sign = tx.getCategory().equalsIgnoreCase("Salary") || tx.getCategory().equalsIgnoreCase("Investment")
                    ? "+" : "-";
            displayItems.add(String.format("%s %s$%.2f", tx.getName(), sign, tx.getAmount()));
        }

        recentTransactions.setItems(displayItems);
    }
    public static class Transaction {
        private String name;
        private String date;
        private double amount;
        private String category;

        public Transaction(String name, String date, double amount, String category) {
            this.name = name;
            this.date = date;
            this.amount = amount;
            this.category = category;
        }

        public String getName() { return name; }
        public String getDate() { return date; }
        public double getAmount() { return amount; }
        public String getCategory() { return category; }
    }

    private List<Transaction> readTransactionsFromCSV(String path) {
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
                    String name = parts[1];
                    String date = parts[2];
                    double amount = Double.parseDouble(parts[3]);
                    String category = parts[4];
                    result.add(new Transaction(name, date, amount, category));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    private void loadBankCards() {
        card1Balance.setText("$5,756.38");
        card1Holder.setText("Eddy Cusuma");
        card2Balance.setText("$2,345.67");
        card2Holder.setText("Shelton Li");
    }
}


