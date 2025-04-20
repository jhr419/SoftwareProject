package com.shelton.ebu6403.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.YearMonth;

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
                new SimpleStringProperty(cellData.getValue().getCategory() + "\n" +
                        cellData.getValue().getTime()));

        TableColumn<Transaction, String> typeCol = (TableColumn<Transaction, String>) transactionsTable.getColumns().get(1);
        typeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getType()));

        TableColumn<Transaction, String> amountCol = (TableColumn<Transaction, String>) transactionsTable.getColumns().get(2);
        amountCol.setCellValueFactory(cellData ->
                new SimpleStringProperty("$" + String.format("%.2f", cellData.getValue().getAmount())));
    }

    private void loadDailyData(LocalDate date) {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList(
                new Transaction("Food", "17:00", "Spend", 70.0),
                new Transaction("Payment", "09:00", "Income", 800.0)
        );
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

        // 柱状图
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(type);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(type + " Income & Expense");
        barChart.setPrefWidth(600);

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");
        incomeSeries.getData().add(new XYChart.Data<>("1", 400));
        incomeSeries.getData().add(new XYChart.Data<>("2", 600));

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expense");
        expenseSeries.getData().add(new XYChart.Data<>("1", 250));
        expenseSeries.getData().add(new XYChart.Data<>("2", 180));

        barChart.getData().addAll(incomeSeries, expenseSeries);

        // 右侧 - 上：均值卡片
        VBox avgBox = new VBox(10);
        avgBox.getChildren().addAll(
                new Label(type + " Average Income: $500"),
                new Label(type + " Average Expense: $300")
        );

        // 右侧 - 下：饼图
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Category Share");
        pieChart.getData().addAll(
                new PieChart.Data("Food", 50),
                new PieChart.Data("Rent", 30),
                new PieChart.Data("Others", 20)
        );

        VBox rightPane = new VBox(10);
        rightPane.getChildren().addAll(avgBox, pieChart);
        rightPane.setPrefWidth(300);

        // 整体排版
        HBox layout = new HBox(20);
        layout.getChildren().addAll(barChart, rightPane);
        pane.getChildren().add(layout);
    }

    public static class Transaction {
        private final String category;
        private final String time;
        private final String type;
        private final double amount;

        public Transaction(String category, String time, String type, double amount) {
            this.category = category;
            this.time = time;
            this.type = type;
            this.amount = amount;
        }

        public String getCategory() { return category; }

        public String getTime() { return time; }

        public String getType() { return type; }

        public double getAmount() { return amount; }
    }
}
