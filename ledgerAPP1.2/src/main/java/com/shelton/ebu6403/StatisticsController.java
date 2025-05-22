package com.shelton.ebu6403;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;

public class StatisticsController {
    @FXML private BarChart<String, Number> incomeExpenseChart;
    @FXML private PieChart categoryChart;
    @FXML private TableView<Transaction> transactionTable;

    public void initialize() {
        // 初始化柱状图
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("收入");
        incomeSeries.getData().add(new XYChart.Data<>("4/15", 1500));

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("支出");
        expenseSeries.getData().add(new XYChart.Data<>("4/15", 800));

        incomeExpenseChart.getData().addAll(incomeSeries, expenseSeries);

        // 初始化饼图
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("食物", 35),
                new PieChart.Data("交通", 25),
                new PieChart.Data("医疗", 25),
                new PieChart.Data("其他", 15)
        );
        categoryChart.setData(pieData);

        // 初始化表格
        transactionTable.getItems().addAll(
                new Transaction("19:15", "MUJI消费", -15.0),
                new Transaction("21:34", "妈妈转账", 850.0)
        );
    }

    public void openLedgerSelection(ActionEvent actionEvent) {
    }

    public static class Transaction {
        private final String time;
        private final String description;
        private final double amount;

        public Transaction(String time, String description, double amount) {
            this.time = time;
            this.description = description;
            this.amount = amount;
        }

        // 构造函数和getters...
    }
}


