package com.shelton.ebu6403.controller;

import com.shelton.ebu6403.models.BudgetSet;
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

import java.time.LocalDate;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Controller for managing expense and income categories.
 * <p>
 * Handles the display and interaction of transaction category cards, transaction tables,
 * and supports operations like importing CSV data, AI-assisted categorization, and
 * transaction CRUD (create, read, update, delete).
 * </p>
 *
 * <p>Main functionalities include:</p>
 * <ul>
 *   <li>Initializing category cards and transaction tables</li>
 *   <li>Importing transactions from CSV with AI classification fallback</li>
 *   <li>Creating, editing, deleting both expense and income entries</li>
 *   <li>Persisting transaction data to CSV</li>
 * </ul>
 *
 * Author: Haoran Jin, Zhifei Liu, Zuhao Zhang
 */
public class CategoriesController {

    @FXML
    FlowPane expensesCardContainer;
    @FXML
    FlowPane incomeCardContainer;
    @FXML
    TableView<Transaction> expensesTable;
    @FXML
    TableView<Transaction> incomeTable;

    private final ExpenseManager expenseManager = new ExpenseManager("sk-cbzpgeqjquxjgusngdklsmrzikmptukukbrvzbjhibsosfyf");

    private final String[] defaultExpenseCategories = {
            "Travel", "Entertainment", "Clothing", "Education",
            "Transportation", "Medical", "Home", "Food",
            "Sports", "Communication", "Others"
    };
    private final String[] defaultIncomeCategories = {
            "Salary", "Investment", "Gift", "Others"
    };
    private final String customExpenseFile = "data/custom_expense_categories.csv";
    private final String customIncomeFile = "data/custom_income_categories.csv";

    final ObservableList<Transaction> allExpenses = FXCollections.observableArrayList();
    final ObservableList<Transaction> allIncomes = FXCollections.observableArrayList();
    private BudgetSet budgetSet = new BudgetSet();

    /**
     * Initializes the category cards and transaction tables.
     * <p>
     * Loads default and custom categories for expenses and incomes,
     * sets up click actions for filtering, and displays existing transactions.
     * </p>
     */
    @FXML
    public void initialize() {
        initCategoryCards();
        initTransactionTables();
        loadSampleData();
    }

    /**
     * Opens a file chooser to import a CSV file of transactions.
     * <p>
     * Automatically assigns serial numbers and uses AI to classify categories
     * if none is provided. Data is added to the expense list and persisted to file.
     * </p>
     */
    @FXML
    private void handleImportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Transactions CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            // 1. get serial number
            int nextSerialNo = allExpenses.size() + 1;

            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                boolean isFirst = true;
                while ((line = reader.readLine()) != null) {
                    if (isFirst) { isFirst = false; continue; } // skip the first line

                    String[] parts = line.split(",", -1);
                    if (parts.length < 4) continue;

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

            // alert fow successful import
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Import Successful");
            alert.setHeaderText(null);
            alert.setContentText("CSV has been imported successfully！");
            alert.showAndWait();

        }
    }

    /**
     * Initializes category cards for both expense and income types.
     * <p>
     * Loads default and custom-defined categories, and creates visual cards for each.
     * Cards are added to their respective containers and configured with click listeners
     * to filter transactions by category. A "More" card is appended for adding new categories.
     * </p>
     */
    private void initCategoryCards() {
        List<String> allExpenseCats = new ArrayList<>(Arrays.asList(defaultExpenseCategories));
        allExpenseCats.addAll(readCustomCategories(customExpenseFile));

        for (String category : allExpenseCats) {
            VBox card = createCategoryCard(category, "/com/shelton/ebu6403/images/icons/");
            card.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> filterTransactions(category, "Expense"));
            expensesCardContainer.getChildren().add(card);
        }

        VBox moreCard = createCategoryCard("More", "/com/shelton/ebu6403/images/icons/");
        moreCard.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> showNewCategoryDialog("Expense"));
        expensesCardContainer.getChildren().add(moreCard);

        // income card
        List<String> allIncomeCats = new ArrayList<>(Arrays.asList(defaultIncomeCategories));
        allIncomeCats.addAll(readCustomCategories(customIncomeFile));

        for (String category : allIncomeCats) {
            VBox card = createCategoryCard(category, "/com/shelton/ebu6403/images/icons/");
            card.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> filterTransactions(category, "Income"));
            incomeCardContainer.getChildren().add(card);
        }

        VBox incomeMoreCard = createCategoryCard("More", "/com/shelton/ebu6403/images/icons/");
        incomeMoreCard.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> showNewCategoryDialog("Income"));
        incomeCardContainer.getChildren().add(incomeMoreCard);
    }

    /**
     * Creates a visual category card with an icon and label.
     * <p>
     * Tries to load an icon image from the specified directory using the category name.
     * If the icon is missing, a fallback emoji is displayed instead. The card is styled
     * and aligned for consistent appearance in the UI.
     * </p>
     *
     * @param title    The display name of the category (e.g., "Food", "Salary").
     * @param iconDir  The directory path where icon images are stored.
     * @return A VBox node representing the formatted category card.
     */
    private VBox createCategoryCard(String title, String iconDir) {
        VBox card = new VBox(5);
        card.getStyleClass().add("category-card");
        card.setAlignment(Pos.CENTER);

        ImageView iconView = new ImageView();
        String formattedName = title.toLowerCase().replaceAll("\\s+", "_");
        String imagePath = iconDir + formattedName + ".png";

        try {
            InputStream imageStream = getClass().getResourceAsStream(imagePath);
            if (imageStream != null) {
                Image icon = new Image(imageStream);
                iconView.setImage(icon);
                iconView.setFitWidth(40);
                iconView.setFitHeight(40);
                card.getChildren().add(iconView);
            } else {
                Label fallbackIcon = new Label("📂");
                fallbackIcon.setStyle("-fx-font-size: 32px;");
                card.getChildren().add(fallbackIcon);
            }
        } catch (Exception e) {
            Label fallbackIcon = new Label("❓");
            fallbackIcon.setStyle("-fx-font-size: 32px;");
            card.getChildren().add(fallbackIcon);
        }

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");
        card.getChildren().add(titleLabel);

        return card;
    }
    /**
     * Appends a single transaction entry to the specified CSV file.
     * <p>
     * If the file does not exist, it creates it and writes the header line first.
     * Ensures the parent directory exists before writing.
     * </p>
     *
     * @param tx       The transaction to append.
     * @param filePath The CSV file path to write to.
     */
    private void appendTransactionToCSV(Transaction tx, String filePath) {
        File file = new File(filePath);
        boolean fileExists = file.exists();

        try {
            // make sure the dictionary exist
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

    /**
     * Overwrites the given CSV file with a new list of transactions.
     * <p>
     * Writes a header line followed by all transaction records, re-indexing serial numbers.
     * </p>
     *
     * @param transactions The list of transactions to write.
     * @param filePath     The CSV file path to overwrite.
     */
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

    /**
     * Initializes the column bindings for both expense and income tables.
     * <p>
     * Sets the property value factories for serial number, name, date,
     * amount, and category columns in each table.
     * </p>
     */
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

    /**
     * Loads sample transaction data from local CSV files.
     * <p>
     * Populates the expense and income tables with transactions
     * read from 'data/expenses.csv' and 'data/incomes.csv'.
     * </p>
     */
    private void loadSampleData() {
        allExpenses.setAll(readTransactionsFromCSV("data/expenses.csv"));
        expensesTable.setItems(allExpenses);

        allIncomes.setAll(readTransactionsFromCSV("data/incomes.csv"));
        incomeTable.setItems(allIncomes);
    }

    /**
     * Filters and displays transactions of a specific category and type.
     *
     * @param category The category to filter by (e.g., "Food", "Salary").
     * @param type     The type of transaction ("Expense" or "Income").
     */
    void filterTransactions(String category, String type) {
        if ("Expense".equals(type)) {
            ObservableList<Transaction> filtered = allExpenses.filtered(t -> category.equals(t.getCategory()));
            expensesTable.setItems(filtered);
        } else {
            ObservableList<Transaction> filtered = allIncomes.filtered(t -> category.equals(t.getCategory()));
            incomeTable.setItems(filtered);
        }
    }

    /**
     * Reads transactions from a CSV file and returns them as an observable list.
     * <p>
     * Skips the header line and parses each transaction into a {@code Transaction} object.
     * </p>
     *
     * @param filePath The path to the CSV file.
     * @return An observable list of {@code Transaction} objects.
     */
    ObservableList<Transaction> readTransactionsFromCSV(String filePath) {
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

    /**
     * Displays a dialog to let the user create a new category.
     * <p>
     * After user input, creates a new category card and adds it to the UI.
     * Also persists the category to the corresponding custom category file.
     * </p>
     *
     * @param type Either "Expense" or "Income" indicating the category type.
     */
    @FXML
    void showNewCategoryDialog(String type) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Category");
        dialog.setHeaderText("Create a new " + type + " category");
        dialog.setContentText("Please enter category name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            VBox card = createCategoryCard(name, "/com/shelton/ebu6403/images/icons/");
            card.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> filterTransactions(name, type));

            if ("Expense".equals(type)) {
                expensesCardContainer.getChildren().add(expensesCardContainer.getChildren().size() - 1, card);
                appendToCustomCategoryFile(customExpenseFile, name);
            } else {
                incomeCardContainer.getChildren().add(incomeCardContainer.getChildren().size() - 1, card);
                appendToCustomCategoryFile(customIncomeFile, name);
            }
        });
    }

    /**
     * Reads custom category names from a text file.
     * <p>
     * Each line in the file represents a single category. Blank lines are ignored.
     * </p>
     *
     * @param filePath The file path containing custom category names.
     * @return A list of category names.
     */
    private List<String> readCustomCategories(String filePath) {
        List<String> categories = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return categories;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) categories.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return categories;
    }

    /**
     * Appends a new custom category to the specified file.
     * <p>
     * Ensures the directory exists before writing. The new category is written on a new line.
     * </p>
     *
     * @param filePath The file to which the category should be appended.
     * @param category The name of the new custom category.
     */
    private void appendToCustomCategoryFile(String filePath, String category) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs(); // make sure data file exist
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(category);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays a dialog for adding a new expense transaction.
     * <p>
     * If the category field is left blank, the system uses AI to suggest a category.
     * The user can confirm or modify the suggestion. The new expense is added to the
     * table and stored in the local CSV file. It is also recorded in the {@code BudgetSet}.
     * </p>
     */
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
            // refresh automatically
            budgetSet.addExpense(tx.getCategory(), LocalDate.parse(tx.getDate()), tx.getAmount());
        });
    }

    /**
     * Displays a dialog for adding a new income transaction.
     * <p>
     * If the category field is left blank, the system uses AI to suggest a category.
     * The user can confirm or modify the suggestion. The new income is added to the
     * table and stored in the local CSV file.
     * </p>
     */
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


    /**
     * Deletes the selected expense transaction from the table and updates the CSV file.
     */
    @FXML
    void handleDeleteExpense() {
        Transaction selected = expensesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            allExpenses.remove(selected);
            expensesTable.setItems(allExpenses);
            overwriteCSV(allExpenses, "data/expenses.csv");
        }
    }

    /**
     * Opens a dialog for editing the selected expense transaction.
     * <p>
     * Updates the expense table and overwrites the CSV file with new values.
     * </p>
     */
    @FXML
    private void handleEditExpense() {
        Transaction selected = expensesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showEditTransactionDialog(selected, "Expense");
            expensesTable.refresh();
            overwriteCSV(allExpenses, "data/expenses.csv");
        }
    }

    /**
     * Displays a dialog for editing a given transaction.
     * <p>
     * Allows the user to update the transaction's name, date, amount, and category.
     * </p>
     *
     * @param tx   The transaction to edit.
     * @param type The type of transaction ("Expense" or "Income") for display labeling.
     */
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

    /**
     * Deletes the selected income transaction from the table and updates the CSV file.
     */
    @FXML
    private void handleDeleteIncome() {
        Transaction selected = incomeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            allIncomes.remove(selected);
            incomeTable.setItems(allIncomes);
            overwriteCSV(allIncomes, "data/incomes.csv");
        }
    }

    /**
     * Opens a dialog for editing the selected income transaction.
     * <p>
     * Updates the income table and overwrites the CSV file with new values.
     * </p>
     */
    @FXML
    private void handleEditIncome() {
        Transaction selected = incomeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showEditTransactionDialog(selected, "Income");
            incomeTable.refresh();
            overwriteCSV(allIncomes, "data/incomes.csv");
        }
    }

    /**
     * Represents a single financial transaction, either income or expense.
     * <p>
     * Stores transaction metadata including serial number, name, date, amount, and category.
     * </p>
     */
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

