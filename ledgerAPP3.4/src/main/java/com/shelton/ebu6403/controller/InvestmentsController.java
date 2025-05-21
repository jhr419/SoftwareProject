package com.shelton.ebu6403.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class InvestmentsController {

    @FXML private HBox cardScrollContainer;
    @FXML private VBox infoSummaryContainer;
    @FXML private VBox investmentListContainer;
    @FXML private VBox aiAssistantCard;
    @FXML private LineChart<String, Number> monthlyRevenueChart;

    @FXML
    public void initialize() {
        initCards();
        initSummary();
        initInvestmentList();
        initChart();
    }

    private void initCards() {
        // 示例卡片 1
        VBox card = createBankCard("$1,646", "Eddy Cusuma", "12/22", "3778 **** **** 1234", true);
        VBox card2 = createBankCard("$991", "Eddy Cusuma", "12/22", "3778 **** **** 1234", false);
        VBox addCard = createAddCard();
        cardScrollContainer.getChildren().addAll(card2, card, addCard);
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
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Add Card");
            dialog.setHeaderText("Credit Card generally means a plastic card issued by Scheduled Commercial Banks\nassigned to a Cardholder, with a credit limit, that can be used to purchase goods\nand services on credit or obtain cash advances.");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setVgap(10); grid.setHgap(10);
            TextField cardType = new TextField("Classic");
            TextField cardName = new TextField("My Cards");
            TextField cardNumber = new TextField("**** **** **** ****");
            DatePicker expiryDate = new DatePicker();

            grid.addRow(0, new Label("Card Type"), cardType);
            grid.addRow(1, new Label("Name On Card"), cardName);
            grid.addRow(2, new Label("Card Number"), cardNumber);
            grid.addRow(3, new Label("Expiration Date"), expiryDate);

            dialog.getDialogPane().setContent(grid);
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSummary() {
        infoSummaryContainer.getChildren().addAll(
                createSummaryCard("Today's Balance", "+$2,140", "#fdd835"),
                createSummaryCard("Today's Income", "+$5,600", "#00c853"),
                createSummaryCard("Today's Loss", "-$3,460", "#d32f2f")
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
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");
        series.getData().add(new XYChart.Data<>("Feb", 5000));
        series.getData().add(new XYChart.Data<>("Mar", 22000));
        series.getData().add(new XYChart.Data<>("Apr", 18000));
        series.getData().add(new XYChart.Data<>("May", 35000));
        series.getData().add(new XYChart.Data<>("Jun", 24000));
        series.getData().add(new XYChart.Data<>("Jul", 29000));
        monthlyRevenueChart.getData().add(series);
    }

    private void initInvestmentList() {
        investmentListContainer.getChildren().addAll(
                createInvestmentItem("Apple Store", "$54,000", "+16%", "E-commerce", "green"),
                createInvestmentItem("Samsung Mobile", "$25,300", "-4%", "Marketplace", "red"),
                createInvestmentItem("Tesla Motors", "$8,200", "+25%", "Electric Vehicles", "green")
        );
    }

    private HBox createInvestmentItem(String name, String amount, String returnValue, String category, String color) {
        HBox row = new HBox(10);
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/com/shelton/ebu6403/images/icon.png")));
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
