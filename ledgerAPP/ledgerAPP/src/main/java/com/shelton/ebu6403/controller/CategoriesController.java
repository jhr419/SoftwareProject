package com.shelton.ebu6403.controller;

import com.shelton.ebu6403.models.ExpenseManager;
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
import javafx.stage.FileChooser;

import java.util.Set;
import java.util.HashSet;
import java.io.*;
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
            "Sports", "Communication","Others"
    };
    private final ExpenseManager expenseManager = new ExpenseManager("sk-cbzpgeqjquxjgusngdklsmrzikmptukukbrvzbjhibsosfyf");

    // 收入分类
    private final String[] incomeCategories = {"Salary", "Investment","Others"};

    private final ObservableList<Transaction> allExpenses = FXCollections.observableArrayList();
    private final ObservableList<Transaction> allIncomes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        initCategoryCards();
        initTransactionTables();
        loadSampleData();
    }

    @FXML
    private void handleImportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Transactions CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            // 1. 从当前列表获取起始编号
            int nextSerialNo = allExpenses.size() + 1;

            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                boolean isFirst = true;
                while ((line = reader.readLine()) != null) {
                    if (isFirst) { isFirst = false; continue; } // 跳过表头

                    String[] parts = line.split(",", -1);
                    if (parts.length < 4) continue;

                    // 忽略 parts[0] 原序号，直接重编号
                    String name = parts[1];
                    String date = parts[2];
                    double amount = Double.parseDouble(parts[3]);
                    String category = (parts.length >= 5 && !parts[4].isBlank())
                            ? parts[4]
                            : expenseManager.classifyWithAI(name, "expense");

                    Transaction tx = new Transaction(nextSerialNo++, name, date, amount, category);
                    allExpenses.add(tx);
                    appendTransactionToCSV(tx, "data/expenses.csv");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            expensesTable.setItems(allExpenses);

            // 导入成功弹窗
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Import Successful");
            alert.setHeaderText(null);
            alert.setContentText("CSV 文件已成功导入！");
            alert.showAndWait();

        }
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
    private void appendTransactionToCSV(Transaction tx, String filePath) {
        File file = new File(filePath);
        boolean fileExists = file.exists();

        try {
            // 确保目录存在
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            FileWriter writer = new FileWriter(file, true);
            if (!fileExists) {
                writer.write("serialNo,name,date,amount,category\n");
            }
            writer.write(String.format("%d,%s,%s,%.2f,%s\n",
                    tx.getSerialNo(), tx.getName(), tx.getDate(),
                    tx.getAmount(), tx.getCategory()));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void overwriteCSV(ObservableList<Transaction> transactions, String filePath) {
        File file = new File(filePath);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("serialNo,name,date,amount,category\n");
            int index = 1;
            for (Transaction tx : transactions) {
                writer.write(String.format("%d,%s,%s,%.2f,%s\n",
                        index++, tx.getName(), tx.getDate(), tx.getAmount(), tx.getCategory()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        TableColumn<Transaction, String> expCategoryCol = (TableColumn<Transaction, String>) expensesTable.getColumns().get(4);
        expCategoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Transaction, Integer> incNoCol = (TableColumn<Transaction, Integer>) incomeTable.getColumns().get(0);
        incNoCol.setCellValueFactory(new PropertyValueFactory<>("serialNo"));

        TableColumn<Transaction, String> incNameCol = (TableColumn<Transaction, String>) incomeTable.getColumns().get(1);
        incNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Transaction, String> incDateCol = (TableColumn<Transaction, String>) incomeTable.getColumns().get(2);
        incDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Transaction, Double> incAmountCol = (TableColumn<Transaction, Double>) incomeTable.getColumns().get(3);
        incAmountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Transaction, String> incCategoryCol = (TableColumn<Transaction, String>) incomeTable.getColumns().get(4);
        incCategoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
    }

    private void loadSampleData() {
        allExpenses.setAll(readTransactionsFromCSV("data/expenses.csv"));
        expensesTable.setItems(allExpenses);

        allIncomes.setAll(readTransactionsFromCSV("data/incomes.csv"));
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

    private ObservableList<Transaction> readTransactionsFromCSV(String filePath) {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        File file = new File(filePath);
        if (!file.exists()) return transactions;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst) { isFirst = false; continue; } // 跳过表头
                String[] parts = line.split(",", -1);
                if (parts.length == 5) {
                    int serialNo = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String date = parts[2];
                    double amount = Double.parseDouble(parts[3]);
                    String category = parts[4];
                    transactions.add(new Transaction(serialNo, name, date, amount, category));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transactions;
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
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle("Add Expense");

        TextField nameField = new TextField();
        TextField dateField = new TextField();
        TextField amountField = new TextField();
        TextField categoryField = new TextField();

        VBox content = new VBox(10,
                new Label("Title:"), nameField,
                new Label("Date:"), dateField,
                new Label("Amount:"), amountField,
                new Label("Category (Leave empty to use AI):"), categoryField
        );
        dialog.getDialogPane().setContent(content);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                String name = nameField.getText();
                String date = dateField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String categoryInput = categoryField.getText();
                String finalCategory;

                if (categoryInput == null || categoryInput.isBlank()) {
                    String aiCategory = expenseManager.classifyWithAI(name, "expense");

                    TextInputDialog confirmDialog = new TextInputDialog(aiCategory);
                    confirmDialog.setTitle("AI Classification Suggestion");
                    confirmDialog.setHeaderText("AI suggests category: " + aiCategory);
                    confirmDialog.setContentText("You can use this category or modify it:");

                    Optional<String> userChoice = confirmDialog.showAndWait();
                    finalCategory = userChoice.orElse("Others");
                } else {
                    finalCategory = categoryInput;
                }

                return new Transaction(
                        allExpenses.size() + 1,
                        name,
                        date,
                        amount,
                        finalCategory
                );
            }
            return null;
        });

        Optional<Transaction> result = dialog.showAndWait();
        result.ifPresent(tx -> {
            allExpenses.add(tx);
            expensesTable.setItems(allExpenses);
            appendTransactionToCSV(tx, "data/expenses.csv");
        });
    }

    @FXML
    private void showAddIncomeDialog() {
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle("Add Income");

        TextField nameField = new TextField();
        TextField dateField = new TextField();
        TextField amountField = new TextField();
        TextField categoryField = new TextField();

        VBox content = new VBox(10,
                new Label("Title:"), nameField,
                new Label("Date:"), dateField,
                new Label("Amount:"), amountField,
                new Label("Category (Leave empty to use AI):"), categoryField
        );
        dialog.getDialogPane().setContent(content);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                String name = nameField.getText();
                String date = dateField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String categoryInput = categoryField.getText();
                String finalCategory;

                if (categoryInput == null || categoryInput.isBlank()) {
                    String aiCategory = expenseManager.classifyWithAI(name, "income");

                    TextInputDialog confirmDialog = new TextInputDialog(aiCategory);
                    confirmDialog.setTitle("AI Classification Suggestion");
                    confirmDialog.setHeaderText("AI suggests category: " + aiCategory);
                    confirmDialog.setContentText("You can use this category or modify it:");

                    Optional<String> userChoice = confirmDialog.showAndWait();
                    finalCategory = userChoice.orElse("Others");
                } else {
                    finalCategory = categoryInput;
                }

                return new Transaction(
                        allIncomes.size() + 1,
                        name,
                        date,
                        amount,
                        finalCategory
                );
            }
            return null;
        });

        Optional<Transaction> result = dialog.showAndWait();
        result.ifPresent(tx -> {
            allIncomes.add(tx);
            incomeTable.setItems(allIncomes);
            appendTransactionToCSV(tx, "data/incomes.csv");
        });
    }


    // 修改后的 CategoriesController 的增删改功能
    @FXML
    private void handleDeleteExpense() {
        Transaction selected = expensesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            allExpenses.remove(selected);
            expensesTable.setItems(allExpenses);
            overwriteCSV(allExpenses, "data/expenses.csv");
        }
    }


    @FXML
    private void handleEditExpense() {
        Transaction selected = expensesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showEditTransactionDialog(selected, "Expense");
            expensesTable.refresh();
            overwriteCSV(allExpenses, "data/expenses.csv");
        }
    }


    private void showEditTransactionDialog(Transaction tx, String type) {
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle("Edit " + type);

        TextField nameField = new TextField(tx.getName());
        TextField dateField = new TextField(tx.getDate());
        TextField amountField = new TextField(String.valueOf(tx.getAmount()));
        TextField categoryField = new TextField(tx.getCategory());

        VBox content = new VBox(10,
                new Label("Title:"), nameField,
                new Label("Date:"), dateField,
                new Label("Amount:"), amountField,
                new Label("Category:"), categoryField
        );
        dialog.getDialogPane().setContent(content);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                tx.setName(nameField.getText());
                tx.setDate(dateField.getText());
                tx.setAmount(Double.parseDouble(amountField.getText()));
                tx.setCategory(categoryField.getText());
                return tx;
            }
            return null;
        });

        dialog.showAndWait();
        expensesTable.refresh();
    }

    @FXML
    private void handleDeleteIncome() {
        Transaction selected = incomeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            allIncomes.remove(selected);
            incomeTable.setItems(allIncomes);
            overwriteCSV(allIncomes, "data/incomes.csv");
        }
    }


    @FXML
    private void handleEditIncome() {
        Transaction selected = incomeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showEditTransactionDialog(selected, "Income");
            incomeTable.refresh();
            overwriteCSV(allIncomes, "data/incomes.csv");
        }
    }

    public ExpenseManager getExpenseManager() {
        return expenseManager;
    }

    public static class Transaction {
        private int serialNo;
        private String name;
        private String date;
        private double amount;
        private String category;

        public Transaction(int serialNo, String name, String date, double amount, String category) {
            this.serialNo = serialNo;
            this.name = name;
            this.date = date;
            this.amount = amount;
            this.category = category;
        }

        public int getSerialNo() { return serialNo; }
        public void setSerialNo(int serialNo) { this.serialNo = serialNo; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }
}

