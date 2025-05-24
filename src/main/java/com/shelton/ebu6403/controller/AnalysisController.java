package com.shelton.ebu6403.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class AnalysisController {

    @FXML private VBox dailyDetailsContainer;
    @FXML private VBox calendarContainer;
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private Label summaryIncome, summarySpend, summaryBalance;

    @FXML private TabPane analysisTabs;
    @FXML private VBox analysisDailyPane;
    @FXML private VBox analysisMonthlyPane;
    @FXML private VBox analysisYearlyPane;

    private YearMonth currentYearMonth = YearMonth.now();

    @FXML
    public void initialize() {
        // Daily 部分初始化
        buildCalendar();
        setupTransactionTable();
        loadDailyData(LocalDate.now());

        // Analysis 部分初始化
        setupAnalysisTabs();
        analysisTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab.getText().equals("Daily")) {
                createAnalysisTabContent(analysisDailyPane, "Daily");
            } else if (newTab.getText().equals("Monthly")) {
                createAnalysisTabContent(analysisMonthlyPane, "Monthly");
            } else if (newTab.getText().equals("Yearly")) {
                createAnalysisTabContent(analysisYearlyPane, "Yearly");
            }
        });

    }

    private void buildCalendar() {
        calendarContainer.getChildren().clear();

        // 月份和年份选择
        HBox header = new HBox(10);
        ComboBox<String> monthCombo = new ComboBox<>(FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        ));
        monthCombo.getSelectionModel().select(currentYearMonth.getMonthValue() - 1);
        Spinner<Integer> yearSpinner = new Spinner<>(2000, 2100, currentYearMonth.getYear());
        yearSpinner.setEditable(true);

        monthCombo.setOnAction(e -> {
            currentYearMonth = YearMonth.of(yearSpinner.getValue(), monthCombo.getSelectionModel().getSelectedIndex() + 1);
            buildCalendar();
        });

        yearSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentYearMonth = YearMonth.of(newVal, monthCombo.getSelectionModel().getSelectedIndex() + 1);
            buildCalendar();
        });

        header.getChildren().addAll(monthCombo, yearSpinner);
        calendarContainer.getChildren().add(header);

        // 星期标题
        GridPane dayNames = new GridPane();
        String[] dayNamesArr = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(dayNamesArr[i]);
            dayLabel.getStyleClass().add("calendar-dayname");
            dayNames.add(dayLabel, i, 0);
        }
        calendarContainer.getChildren().add(dayNames);

        // 日期按钮
        GridPane calendarGrid = new GridPane();
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0

        for (int day = 1; day <= currentYearMonth.lengthOfMonth(); day++) {
            Button dayBtn = new Button(String.valueOf(day));
            dayBtn.getStyleClass().add("calendar-day");
            int finalDay = day;
            dayBtn.setOnAction(e -> loadDailyData(currentYearMonth.atDay(finalDay)));

            int row = (dayOfWeek + day - 1) / 7;
            int col = (dayOfWeek + day - 1) % 7;
            calendarGrid.add(dayBtn, col, row);
        }
        calendarContainer.getChildren().add(calendarGrid);
    }

    private void setupTransactionTable() {
        TableColumn<Transaction, String> categoryCol = (TableColumn<Transaction, String>) transactionsTable.getColumns().get(0);
        categoryCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory()));

        TableColumn<Transaction, String> typeCol = (TableColumn<Transaction, String>) transactionsTable.getColumns().get(1);
        typeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getType()));

        TableColumn<Transaction, String> amountCol = (TableColumn<Transaction, String>) transactionsTable.getColumns().get(2);
        amountCol.setCellValueFactory(cellData ->
                new SimpleStringProperty("$" + String.format("%.2f", cellData.getValue().getAmount())));
    }
    List<Transaction> readTransactionsFromCSV(String path, String type, LocalDate targetDate) {
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
                    String dateStr = parts[2]; // 2025-05-01
                    double amount = Double.parseDouble(parts[3]);
                    String category = parts[4];

                    if (dateStr.equals(targetDate.toString())) {
                        result.add(new Transaction(category, type, amount, name, dateStr));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void loadDailyData(LocalDate date) {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();

        // 读取所有数据（支出）
        transactions.addAll(readTransactionsFromCSV("data/expenses.csv", "Spend", date));

        // 读取所有数据（收入）
        transactions.addAll(readTransactionsFromCSV("data/incomes.csv", "Income", date));

        transactionsTable.setItems(transactions);

        double income = transactions.stream()
                .filter(t -> "Income".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double spend = transactions.stream()
                .filter(t -> "Spend".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        summaryIncome.setText(String.format("Income: $%.2f", income));
        summarySpend.setText(String.format("Spend: $%.2f", spend));
        summaryBalance.setText(String.format("Balance: $%.2f", income - spend));
    }


    private void setupAnalysisTabs() {
        createAnalysisTabContent(analysisDailyPane, "Daily");
        createAnalysisTabContent(analysisMonthlyPane, "Monthly");
        createAnalysisTabContent(analysisYearlyPane, "Yearly");
    }

    private void createAnalysisTabContent(VBox pane, String type) {

        pane.getChildren().clear();

        // 柱状图（收入+支出）
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(type);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(type + " Income & Expense");
        barChart.setPrefWidth(600);

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expense");

        // 加载柱状图数据（按日、月或年）
        Map<String, Double> incomeData = new LinkedHashMap<>();
        Map<String, Double> expenseData = new LinkedHashMap<>();

        switch (type) {
            case "Daily":
                incomeData = aggregateByDayOrMonthOrYear("data/incomes.csv", "day");
                expenseData = aggregateByDayOrMonthOrYear("data/expenses.csv", "day");
                break;
            case "Monthly":
                incomeData = aggregateByDayOrMonthOrYear("data/incomes.csv", "month");
                expenseData = aggregateByDayOrMonthOrYear("data/expenses.csv", "month");
                break;
            case "Yearly":
                incomeData = aggregateByDayOrMonthOrYear("data/incomes.csv", "year");
                expenseData = aggregateByDayOrMonthOrYear("data/expenses.csv", "year");
                break;
        }

        List<Integer> days = incomeData.keySet().stream()
                .map(Integer::parseInt)
                .sorted()
                .collect(Collectors.toList());

        for (Integer day : days) {
            String key = String.valueOf(day);
            incomeSeries.getData().add(new XYChart.Data<>(key, incomeData.getOrDefault(key, 0.0)));
            expenseSeries.getData().add(new XYChart.Data<>(key, expenseData.getOrDefault(key, 0.0)));
        }


        barChart.getData().addAll(incomeSeries, expenseSeries);

        // 右侧信息：均值卡片（静态或后续可动态计算）
        VBox avgBox = new VBox(10);

        double incomeSum = incomeData.values().stream().mapToDouble(d -> d).sum();
        double expenseSum = expenseData.values().stream().mapToDouble(d -> d).sum();
        int count = incomeData.size(); // 或 expenseData.size()

        // 防止除以 0
        int divider = Math.max(count, 1);

        Label avgIncome = new Label(type + " Average Income: $" + String.format("%.2f", incomeSum / divider));
        Label avgExpense = new Label(type + " Average Expense: $" + String.format("%.2f", expenseSum / divider));

        avgBox.getChildren().addAll(avgIncome, avgExpense);


        // 饼图：仅统计当前月的支出分类
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Category Share");

        Map<String, Double> expenseByCategory = aggregateByCategoryForMonth("data/expenses.csv", LocalDate.now().getMonthValue());
        for (Map.Entry<String, Double> entry : expenseByCategory.entrySet()) {
            pieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        VBox rightPane = new VBox(10);
        rightPane.getChildren().addAll(avgBox, pieChart);
        rightPane.setPrefWidth(300);

        // 最终布局
        HBox layout = new HBox(20);
        layout.getChildren().addAll(barChart, rightPane);
        pane.getChildren().add(layout);
    }


    private Map<Integer, Double> aggregateByDay(String filePath) {
        Map<Integer, Double> result = new HashMap<>();
        File file = new File(filePath);
        if (!file.exists()) return result;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst) { isFirst = false; continue; }
                String[] parts = line.split(",", -1);
                if (parts.length >= 3) {
                    String date = parts[2]; // yyyy-MM-dd
                    int day = Integer.parseInt(date.substring(8, 10));
                    double amount = Double.parseDouble(parts[3]);
                    result.put(day, result.getOrDefault(day, 0.0) + amount);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    Map<String, Double> aggregateByDayOrMonthOrYear(String filePath, String mode) {
        Map<String, Double> result = new LinkedHashMap<>();
        File file = new File(filePath);
        if (!file.exists()) return result;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst) { isFirst = false; continue; }
                String[] parts = line.split(",", -1);
                if (parts.length >= 3) {
                    String dateStr = parts[2]; // yyyy-MM-dd
                    LocalDate date = LocalDate.parse(dateStr);

                    // 筛选当前范围
                    LocalDate now = LocalDate.now();
                    boolean include = switch (mode) {
                        case "day" -> date.getYear() == now.getYear() && date.getMonthValue() == now.getMonthValue();
                        case "month" -> date.getYear() == now.getYear();
                        case "year" -> true;
                        default -> false;
                    };

                    if (!include) continue;

                    String key = switch (mode) {
                        case "day" -> String.valueOf(date.getDayOfMonth());
                        case "month" -> String.valueOf(date.getMonthValue());
                        case "year" -> String.valueOf(date.getYear());
                        default -> "";
                    };

                    double amount = Double.parseDouble(parts[3]);
                    result.put(key, result.getOrDefault(key, 0.0) + amount);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }



    private Map<String, Double> aggregateByCategoryForMonth(String filePath, int targetMonth) {
        Map<String, Double> result = new HashMap<>();
        File file = new File(filePath);
        if (!file.exists()) return result;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst) { isFirst = false; continue; }
                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    String dateStr = parts[2]; // yyyy-MM-dd
                    LocalDate date = LocalDate.parse(dateStr);
                    if (date.getMonthValue() != targetMonth) continue;

                    String category = parts[4];
                    double amount = Double.parseDouble(parts[3]);
                    result.put(category, result.getOrDefault(category, 0.0) + amount);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    private Map<String, Double> aggregateByCategory(String filePath) {
        Map<String, Double> result = new HashMap<>();
        File file = new File(filePath);
        if (!file.exists()) return result;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst) { isFirst = false; continue; }
                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    String category = parts[4];
                    double amount = Double.parseDouble(parts[3]);
                    result.put(category, result.getOrDefault(category, 0.0) + amount);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static class Transaction {
        private final String category;
        private final String type;
        private final double amount;
        private final String name;
        private final String date;

        public Transaction(String category, String type, double amount, String name, String date) {
            this.category = category;
            this.type = type;
            this.amount = amount;
            this.name = name;
            this.date = date;
        }

        public String getCategory() { return category; }
        public String getType() { return type; }
        public double getAmount() { return amount; }
        public String getName() { return name; }
        public String getDate() { return date; }
    }

}
