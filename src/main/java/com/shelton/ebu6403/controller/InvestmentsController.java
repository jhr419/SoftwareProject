package com.shelton.ebu6403.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InvestmentsController {

    @FXML private HBox cardScrollContainer;
    @FXML private VBox infoSummaryContainer;
    @FXML private VBox investmentListContainer;
    @FXML private VBox aiAssistantCard;
    @FXML private LineChart<String, Number> monthlyRevenueChart;
    @FXML private DatePicker summaryDatePicker;
    @FXML private VBox monthlyDataContainer;
    @FXML private DatePicker investmentDatePicker;

    @FXML
    public void initialize() {
        initCards();
        initSummary();
        initChart();

        LocalDate today = LocalDate.now();

        // 禁用 summaryDatePicker 未来日期
        summaryDatePicker.setValue(today);
        summaryDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isAfter(today)) {
                    setDisable(true);
                }
            }
        });
        summaryDatePicker.valueProperty().addListener((obs, o, n) -> {
            if (n != null) updateSummaryForDate(n);
        });

        // 禁用 investmentDatePicker 未来日期
        investmentDatePicker.setValue(today);
        investmentDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isAfter(today)) {
                    setDisable(true);
                }
            }
        });
        investmentDatePicker.valueProperty().addListener((obs, o, n) -> {
            if (n != null) initInvestmentList(n);
        });

        // 第一次加载
        initInvestmentList(today);
    }


    private void updateSummaryForDate(LocalDate date) {
        infoSummaryContainer.getChildren().clear();

        String path = "data/investment.csv";
        double income = 0;
        double expense = 0;

        File file = new File(path);
        if (!file.exists()) {
            System.err.println("investment.csv not found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length >= 3) {
                    String dateStr = parts[0].trim();
                    String incomeStr = parts[1].trim();
                    String expenseStr = parts[2].trim();

                    if (dateStr.equals(date.toString())) {
                        income += Double.parseDouble(incomeStr);
                        expense += Double.parseDouble(expenseStr);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        double balance = income - expense;
        infoSummaryContainer.getChildren().addAll(
                createSummaryCard("Balance for " + date, String.format("%+.2f", balance), balance >= 0 ? "#fdd835" : "#d32f2f"),
                createSummaryCard("Income", String.format("+%.2f", income), "#00c853"),
                createSummaryCard("Expense", String.format("-%.2f", expense), "#d32f2f")
        );
    }

    private void initCards() {
        cardScrollContainer.getChildren().clear();
        int index = 0;
        int lastIndex = -1;
        List<VBox> cardList = new ArrayList<>();
        File file = new File("data/cards.csv");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 5) {
                        String type = parts[0];
                        String name = parts[1];
                        String number = parts[2];
                        String expiry = parts[3];
                        String balance = "$" + parts[4];

                        VBox card = createBankCard(balance, name, expiry, number, false);
                        cardList.add(card);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

// 全部添加后，选中最后一个卡片
            if (!cardList.isEmpty()) {
                VBox lastCard = cardList.get(cardList.size() - 1);
                lastCard.getStyleClass().clear();
                lastCard.getStyleClass().add("bank-card-selected");
            }
            cardScrollContainer.getChildren().addAll(cardList);
        }

        // 添加已有默认卡片（如你之前硬编码的）或保留新增卡片
        VBox addCard = createAddCard();
        cardScrollContainer.getChildren().add(addCard);
    }


    private VBox createBankCard(String balance, String holder, String expiry, String number, boolean selected) {
        VBox card = new VBox();
        card.getStyleClass().add(selected ? "bank-card-selected" : "bank-card");
        Label balanceLabel = new Label("Balance\n" + balance);
        Label holderLabel = new Label("CARD HOLDER\n" + holder);
        Label validLabel = new Label("VALID THRU\n" + expiry);
        Label numberLabel = new Label(number);
        card.getChildren().addAll(balanceLabel, holderLabel, validLabel, numberLabel);
        return card;
    }

    private VBox createAddCard() {
        VBox add = new VBox();
        add.getStyleClass().add("add-card");
        Label plus = new Label("+");
        plus.getStyleClass().add("add-icon");
        add.getChildren().add(plus);
        add.setOnMouseClicked(e -> showAddCardDialog());
        return add;
    }

    private void showAddCardDialog() {
        try {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Add Card");
            dialog.setHeaderText("Credit Card generally means a plastic card issued by Scheduled Commercial Banks...");

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setVgap(10);
            grid.setHgap(10);

            TextField cardType = new TextField("Classic");
            TextField cardName = new TextField("My Cards");
            TextField cardNumber = new TextField("**** **** **** ****");
            DatePicker expiryDate = new DatePicker();

            grid.addRow(0, new Label("Card Type"), cardType);
            grid.addRow(1, new Label("Name On Card"), cardName);
            grid.addRow(2, new Label("Card Number"), cardNumber);
            grid.addRow(3, new Label("Expiration Date"), expiryDate);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    String type = cardType.getText().trim();
                    String name = cardName.getText().trim();
                    String number = cardNumber.getText().trim();
                    String expiry = expiryDate.getValue() != null ? expiryDate.getValue().getMonthValue() + "/" + (expiryDate.getValue().getYear() % 100) : "N/A";

                    saveCardToFile(type, name, number, expiry);
                    refreshCards(); // 重新加载卡片视图
                }
                return null;
            });

            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshCards() {
        initCards(); // 简单调用已有的卡片初始化方法
    }


    private void saveCardToFile(String type, String name, String number, String expiry) {
        String balance = "1000"; // 写死或将来可输入
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/cards.csv", true))) {
            writer.write(String.join(",", type, name, number, expiry, balance));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void initSummary() {
        infoSummaryContainer.getChildren().clear(); // 清除旧卡片

        String path = "data/investment.csv";
        LocalDate today = LocalDate.now();
        double income = 0;
        double expense = 0;

        File file = new File(path);
        if (!file.exists()) {
            System.err.println("investment.csv not found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length >= 3) {
                    String dateStr = parts[0].trim();
                    String incomeStr = parts[1].trim();
                    String expenseStr = parts[2].trim();

                    if (dateStr.equals(today.toString())) {
                        income += Double.parseDouble(incomeStr);
                        expense += Double.parseDouble(expenseStr);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        double balance = income - expense;
        System.out.println("initSummary loaded: income=" + income + ", expense=" + expense);

        infoSummaryContainer.getChildren().addAll(
                createSummaryCard("Today's Balance", String.format("%+.2f", balance), balance >= 0 ? "#fdd835" : "#d32f2f"),
                createSummaryCard("Today's Income", String.format("+%.2f", income), "#00c853"),
                createSummaryCard("Today's Loss", String.format("-%.2f", expense), "#d32f2f")
        );
    }



    private VBox createSummaryCard(String title, String value, String color) {
        VBox box = new VBox();
        Label titleLabel = new Label(title);
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: " + color);
        box.getChildren().addAll(titleLabel, valueLabel);
        box.getStyleClass().add("summary-card");
        return box;
    }

    private void initChart() {
        String path = "data/investment.csv";
        Map<Integer, Double> monthlyRevenue = new HashMap<>();

        File file = new File(path);
        if (!file.exists()) {
            System.err.println("investment.csv not found.");
            return;
        }

        // 读取并按月累计 revenue
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst) { isFirst = false; continue; }
                String[] parts = line.split(",", -1);
                if (parts.length >= 3) {
                    LocalDate date = LocalDate.parse(parts[0].trim());
                    int month = date.getMonthValue();
                    double income = Double.parseDouble(parts[1].trim());
                    double expense = Double.parseDouble(parts[2].trim());
                    double revenue = income - expense;

                    // 只统计不晚于今天的月份
                    if (!date.isAfter(LocalDate.now())) {
                        monthlyRevenue.put(month,
                                monthlyRevenue.getOrDefault(month, 0.0) + revenue);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 准备绘图
        monthlyRevenueChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");

        // 只画到当前月
        int thisMonth = LocalDate.now().getMonthValue();
        int maxMonth = Math.min(thisMonth, 6);  // 你的图表只关心 1–6 月

        for (int month = 1; month <= maxMonth; month++) {
            double rev = monthlyRevenue.getOrDefault(month, 0.0);
            String monthStr = String.format("%02d", month);
            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(monthStr, rev);
            series.getData().add(dataPoint);

            // 添加数据标签
            dataPoint.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    Label label = new Label(String.format("$%.0f", rev));
                    label.setStyle("-fx-font-size: 11px; -fx-text-fill: #444;");
                    StackPane.setAlignment(label, Pos.TOP_CENTER);
                    ((StackPane) newNode).getChildren().add(label);
                }
            });
        }

        monthlyRevenueChart.getData().add(series);
    }



    private void initInvestmentList(LocalDate targetDate) {
        investmentListContainer.getChildren().clear();

        File file = new File("data/investments.csv");
        if (!file.exists()) {
            System.err.println("investments.csv not found");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst) { isFirst = false; continue; }

                String[] parts = line.split(",", -1);
                if (parts.length >= 5 && parts[0].equals(targetDate.toString())) {
                    String company = parts[1];
                    String category = parts[2];
                    String price = parts[3];
                    double change = Double.parseDouble(parts[4]);

                    String changeStr = (change >= 0 ? "+" : "") + (int) (change * 100) + "%";
                    String color = change >= 0 ? "green" : "red";

                    investmentListContainer.getChildren().add(
                            createInvestmentItem(company, "$" + price, changeStr, category, color)
                    );
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private HBox createInvestmentItem(String name, String amount, String returnValue, String category, String color) {
        HBox row = new HBox(10);
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/com/shelton/ebu6403/images/profile photo.png")));
        icon.setFitWidth(32); icon.setFitHeight(32);
        VBox desc = new VBox(new Label(name), new Label(category));
        Label amountLabel = new Label(amount);
        Label returnLabel = new Label(returnValue);
        returnLabel.setStyle("-fx-text-fill: " + color);
        row.getChildren().addAll(icon, desc, amountLabel, returnLabel);
        row.getStyleClass().add("investment-item");
        return row;
    }
    @FXML
    private void goToAiPage() {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(
                    getClass().getResource("/com/shelton/ebu6403/views/AiDeepseekView.fxml")
            );

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("AI + DEEPSEEK-R1");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
