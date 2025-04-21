package com.shelton.ebu6403.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Optional;

public class CategoriesController {

    @FXML private FlowPane expensesCardContainer;
    @FXML private FlowPane incomeCardContainer;
    @FXML private TableView<Transaction> expensesTable;
    @FXML private TableView<Transaction> incomeTable;

    // 支出分类
    private final String[] expenseCategories = {
            "Travel", "Entertainment", "Clothing", "Education",
            "Transportation", "Medical", "Home", "Food",
            "Sports", "Communication", "Others"
    };

    // 收入分类
    private final String[] incomeCategories = {"Salary", "Investment"};

    private final ObservableList<Transaction> allExpenses = FXCollections.observableArrayList();
    private final ObservableList<Transaction> allIncomes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        initCategoryCards();
        initTransactionTables();
        loadSampleData();
    }

    private void initCategoryCards() {
        // 创建支出卡片
        for (String category : expenseCategories) {
            VBox card = createCategoryCard(category, "/com/shelton/ebu6403/images/icon.png");
            card.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> filterTransactions(category, "Expense"));
            expensesCardContainer.getChildren().add(card);
        }

        VBox moreCard = createCategoryCard("More", "/com/shelton/ebu6403/images/icon.png");
        moreCard.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> showNewCategoryDialog("Expense"));
        expensesCardContainer.getChildren().add(moreCard);

        // 创建收入卡片
        for (String category : incomeCategories) {
            VBox card = createCategoryCard(category, "/com/shelton/ebu6403/images/icon.png");
            card.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> filterTransactions(category, "Income"));
            incomeCardContainer.getChildren().add(card);
        }

        VBox incomeMoreCard = createCategoryCard("More", "/com/shelton/ebu6403/images/icon.png");
        incomeMoreCard.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> showNewCategoryDialog("Income"));
        incomeCardContainer.getChildren().add(incomeMoreCard);
    }

    private VBox createCategoryCard(String title, String iconPath) {
        VBox card = new VBox(5);
        card.getStyleClass().add("category-card");
        card.setAlignment(Pos.CENTER);

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
        icon.setFitWidth(40);
        icon.setFitHeight(40);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");

        card.getChildren().addAll(icon, titleLabel);
        return card;
    }

    private void initTransactionTables() {
        TableColumn<Transaction, Integer> expNoCol = (TableColumn<Transaction, Integer>) expensesTable.getColumns().get(0);
        expNoCol.setCellValueFactory(new PropertyValueFactory<>("serialNo"));

        TableColumn<Transaction, String> expNameCol = (TableColumn<Transaction, String>) expensesTable.getColumns().get(1);
        expNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Transaction, String> expDateCol = (TableColumn<Transaction, String>) expensesTable.getColumns().get(2);
        expDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Transaction, Double> expAmountCol = (TableColumn<Transaction, Double>) expensesTable.getColumns().get(3);
        expAmountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Transaction, Integer> incNoCol = (TableColumn<Transaction, Integer>) incomeTable.getColumns().get(0);
        incNoCol.setCellValueFactory(new PropertyValueFactory<>("serialNo"));

        TableColumn<Transaction, String> incNameCol = (TableColumn<Transaction, String>) incomeTable.getColumns().get(1);
        incNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Transaction, String> incDateCol = (TableColumn<Transaction, String>) incomeTable.getColumns().get(2);
        incDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Transaction, Double> incAmountCol = (TableColumn<Transaction, Double>) incomeTable.getColumns().get(3);
        incAmountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
    }

    private void loadSampleData() {
        allExpenses.setAll(
                new Transaction(1, "Taxi", "19:27-April 30", 6.40, "Transportation"),
                new Transaction(2, "Shared Bike", "10:07-April 30", 0.67, "Transportation"),
                new Transaction(3, "Uber", "16:05-April 29", 10.63, "Transportation")
        );
        expensesTable.setItems(allExpenses);

        allIncomes.setAll(
                new Transaction(1, "Salary", "01-April 30", 3000.00, "Salary"),
                new Transaction(2, "Dividend", "15-April 30", 150.50, "Investment")
        );
        incomeTable.setItems(allIncomes);
    }

    private void filterTransactions(String category, String type) {
        if ("Expense".equals(type)) {
            ObservableList<Transaction> filtered = allExpenses.filtered(t -> category.equals(t.getCategory()));
            expensesTable.setItems(filtered);
        } else {
            ObservableList<Transaction> filtered = allIncomes.filtered(t -> category.equals(t.getCategory()));
            incomeTable.setItems(filtered);
        }
    }

    @FXML
    private void showNewCategoryDialog(String type) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Category");
        dialog.setHeaderText("Create a new " + type + " category");
        dialog.setContentText("Please enter category name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            VBox card = createCategoryCard(name, "/com/shelton/ebu6403/images/icon.png");
            card.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> filterTransactions(name, type));
            if ("Expense".equals(type)) {
                expensesCardContainer.getChildren().add(expensesCardContainer.getChildren().size() - 1, card);
            } else {
                incomeCardContainer.getChildren().add(incomeCardContainer.getChildren().size() - 1, card);
            }
        });
    }

    @FXML
    private void showAddExpenseDialog() {
        // 可替换为自定义对话框界面
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle("Add Expense");

        Label nameLabel = new Label("Title:");
        TextField nameField = new TextField();

        Label dateLabel = new Label("Date:");
        TextField dateField = new TextField();

        Label amountLabel = new Label("Amount:");
        TextField amountField = new TextField();

        Label descLabel = new Label("Description:");
        TextArea descArea = new TextArea();

        VBox content = new VBox(10, nameLabel, nameField, dateLabel, dateField, amountLabel, amountField, descLabel, descArea);
        dialog.getDialogPane().setContent(content);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                return new Transaction(allExpenses.size() + 1,
                        nameField.getText(),
                        dateField.getText(),
                        Double.parseDouble(amountField.getText()),
                        "Custom");
            }
            return null;
        });

        Optional<Transaction> result = dialog.showAndWait();
        result.ifPresent(tx -> {
            allExpenses.add(tx);
            expensesTable.setItems(allExpenses);
        });
    }

    @FXML
    private void showAddIncomeDialog() {
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle("Add Income");

        Label nameLabel = new Label("Title:");
        TextField nameField = new TextField();

        Label dateLabel = new Label("Date:");
        TextField dateField = new TextField();

        Label amountLabel = new Label("Amount:");
        TextField amountField = new TextField();

        Label descLabel = new Label("Description:");
        TextArea descArea = new TextArea();

        VBox content = new VBox(10, nameLabel, nameField, dateLabel, dateField, amountLabel, amountField, descLabel, descArea);
        dialog.getDialogPane().setContent(content);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                return new Transaction(allIncomes.size() + 1,
                        nameField.getText(),
                        dateField.getText(),
                        Double.parseDouble(amountField.getText()),
                        "Custom");
            }
            return null;
        });

        Optional<Transaction> result = dialog.showAndWait();
        result.ifPresent(tx -> {
            allIncomes.add(tx);
            incomeTable.setItems(allIncomes);
        });
    }

    public static class Transaction {
        private final int serialNo;
        private final String name;
        private final String date;
        private final double amount;
        private final String category;

        public Transaction(int serialNo, String name, String date, double amount, String category) {
            this.serialNo = serialNo;
            this.name = name;
            this.date = date;
            this.amount = amount;
            this.category = category;
        }

        public int getSerialNo() { return serialNo; }
        public String getName() { return name; }
        public String getDate() { return date; }
        public double getAmount() { return amount; }
        public String getCategory() { return category; }
    }
}

