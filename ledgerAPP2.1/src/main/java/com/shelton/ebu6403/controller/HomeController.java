package com.shelton.ebu6403.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;

public class HomeController {
    // 卡片金额显示
    @FXML
    private Label foodAmount;
    @FXML private Label transportAmount;
    @FXML private Label medicineAmount;
    @FXML private Label giftsAmount;

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
        foodAmount.setText("$49.20");
        transportAmount.setText("$30.40");
        medicineAmount.setText("$25.21");
        giftsAmount.setText("$15.00");
    }

    private void configureWeeklyChart() {
        // 设置Y轴格式
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis, "$", null));

        // 创建支出系列（红色）
        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expense");
        expenseSeries.getData().addAll(
                new XYChart.Data<>("Mon", 450),
                new XYChart.Data<>("Tue", 680),
                new XYChart.Data<>("Wed", 920),
                new XYChart.Data<>("Thu", 330),
                new XYChart.Data<>("Fri", 790),
                new XYChart.Data<>("Sat", 1240),
                new XYChart.Data<>("Sun", 580)
        );

        // 创建收入系列（绿色）
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");
        incomeSeries.getData().addAll(
                new XYChart.Data<>("Mon", 600),
                new XYChart.Data<>("Tue", 750),
                new XYChart.Data<>("Wed", 850),
                new XYChart.Data<>("Thu", 400),
                new XYChart.Data<>("Fri", 900),
                new XYChart.Data<>("Sat", 1500),
                new XYChart.Data<>("Sun", 700)
        );

        // 清除旧数据并添加新系列
        weeklyChart.getData().clear();
        weeklyChart.getData().addAll(expenseSeries, incomeSeries);

        // 应用CSS样式
        weeklyChart.setStyle("-fx-bar-fill-0: #e74c3c; -fx-bar-fill-1: #2ecc71;");

        // 添加数据标签
        for (XYChart.Series<String, Number> series : weeklyChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                StackPane node = (StackPane) data.getNode();
                Label label = new Label("$" + data.getYValue());
                label.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px;");
                node.getChildren().add(label);
            }
        }
    }

    private void initExpenseChart() {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Food", 35),
                new PieChart.Data("Transport", 25),
                new PieChart.Data("Medicine", 15),
                new PieChart.Data("Others", 25)
        );
        expenseChart.setData(pieData);
    }

    private void loadTransactions() {
        ObservableList<String> transactions = FXCollections.observableArrayList(
                "Transfer from Mom +$850",
                "MUJI -$15.00",
                "McDonald's -$13.90"
        );
        recentTransactions.setItems(transactions);
    }

    private void loadBankCards() {
        card1Balance.setText("$5,756.38");
        card1Holder.setText("Eddy Cusuma");
        card2Balance.setText("$2,345.67");
        card2Holder.setText("Shelton Li");
    }
}


