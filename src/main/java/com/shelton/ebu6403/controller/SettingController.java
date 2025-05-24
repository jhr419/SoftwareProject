package com.shelton.ebu6403.controller;

import com.shelton.ebu6403.models.*;
import javafx.beans.property.*;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.*;
import javafx.scene.chart.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.YearMonth;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;

import java.lang.Thread;

/**
 * Controller class for managing application settings and budget configurations.
 * <p>
 * Handles budget settings, expense categories, and AI-powered spending insights.
 * </p>
 * @author Haoran Jin, Zhifei liu， Weicheng Xie
 */
public class SettingController {
    /** Top navigation tabs */
    @FXML private TabPane settingsTabPane;

    /** Budget setting components */
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<Integer> yearCombo;
    @FXML private ComboBox<String> monthCombo;
    @FXML private TextField budgetAmountField;
    @FXML private TableView<Budget> budgetTable;
    @FXML private FlowPane budgetCardsContainer;
    @FXML private ProgressIndicator insightProgress;
    @FXML private Label holidayReminderLabel;
    @FXML private Button insightButton;

    private final ObservableList<Budget> budgets = FXCollections.observableArrayList();
    private final FilteredList<Budget> filteredBudgets = new FilteredList<>(budgets);
    private final BudgetSet budgetSet = new BudgetSet();
    private final ExpenseManager expenseManager = new ExpenseManager("sk-cbzpgeqjquxjgusngdklsmrzikmptukukbrvzbjhibsosfyf");

    private final String customExpenseFile = "data/custom_expense_categories.csv";

    private final String[] months = {
            "January", "February", "March", "April",
            "May", "June", "July", "August",
            "September", "October", "November", "December"
    };

    /**
     * Initializes the controller.
     * Sets up settings tabs, budget configurations, and expense monitoring.
     */
    @FXML
    public void initialize() {
        initSettingsTabs();
        initBudgetSettings();
        categoryCombo.setValue("Select All");
        updateMonthFilter();

        // Register expense change listener
        budgetSet.setOnExpenseChanged(() -> {
            refreshBudgetCards();
            budgetTable.refresh();
        });

        // Display holiday reminder on startup (non-popup)
        ChineseHolidayAnalyzer analyzer = new ChineseHolidayAnalyzer(expenseManager.getExpenses());
        String reminder = analyzer.getUpcomingHolidayReminder();
        if (!reminder.isBlank()) {
            holidayReminderLabel.setText("⚠ " + reminder);
            holidayReminderLabel.setVisible(true);
        }
    }

    /**
     * Initializes settings tabs.
     * Creates and adds the main setting sections as tabs.
     */
    private void initSettingsTabs() {
        settingsTabPane.getTabs().addAll(
                createTab("Edit Account"),
                createTab("Budget Settings"),
                createTab("Security"),
                createTab("General"),
                createTab("Help")
        );
    }

    /**
     * Creates a new tab with the specified text.
     * @param text The tab label text
     * @return A new non-closeable Tab instance
     */
    private Tab createTab(String text) {
        Tab tab = new Tab(text);
        tab.setClosable(false);
        return tab;
    }

    /**
     * Initializes budget settings.
     * Sets up year, month, and category selectors, budget table, and filtering.
     */
    private void initBudgetSettings() {
        // Initialize year selection (current year + next 5 years)
        int currentYear = LocalDate.now().getYear();
        yearCombo.setItems(FXCollections.observableArrayList(
                currentYear, currentYear + 1, currentYear + 2,
                currentYear + 3, currentYear + 4, currentYear + 5
        ));
        yearCombo.getSelectionModel().select(0);

        // Initialize month selection
        monthCombo.setItems(FXCollections.observableArrayList(months));
        monthCombo.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);

        // Initialize category selection
        List<String> allCats = new ArrayList<>(List.of(
                "Travel", "Entertainment", "Clothing", "Education", "Transportation",
                "Medical", "Home", "Food", "Sports", "Communication", "Others"
        ));
        allCats.addAll(readCustomCategories(customExpenseFile)); // Add custom categories
        ObservableList<String> categoryOptions = FXCollections.observableArrayList(allCats);
        categoryOptions.add(0, "Select All"); // Add "Select All" option
        categoryCombo.setItems(categoryOptions);
        categoryCombo.getSelectionModel().selectFirst(); // Default to "Select All"

        // Initialize budget table
        initBudgetTable();

        // Load existing data
        loadBudgetFromBudgetSet();

        // Initialize filtering
        setupFiltering();
    }

    /**
     * Reads custom categories from a file.
     * @param filePath The file path to read from
     * @return A list of custom categories
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
     * Initializes the budget table.
     * Sets up columns and data bindings.
     */
    private void initBudgetTable() {
        // Clear old columns
        budgetTable.getColumns().clear();

        // SL No column
        TableColumn<Budget, Number> slNoCol = new TableColumn<>("SL No");
        slNoCol.setCellValueFactory(col -> new ReadOnlyIntegerWrapper(filteredBudgets.indexOf(col.getValue()) + 1));
        slNoCol.setPrefWidth(60);

        // Category column
        TableColumn<Budget, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        categoryCol.setPrefWidth(120);

        // Month column
        TableColumn<Budget, String> monthCol = new TableColumn<>("Month");
        monthCol.setCellValueFactory(cellData -> cellData.getValue().monthProperty());
        monthCol.setPrefWidth(150);

        // Budget column
        TableColumn<Budget, Double> amountCol = new TableColumn<>("Budget");
        amountCol.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
        amountCol.setPrefWidth(100);

        // Action column
        TableColumn<Budget, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final HBox actionBox = new HBox(5);
            private final Button editBtn = new Button("Edit");
            private final Button removeBtn = new Button("Remove");

            {
                editBtn.getStyleClass().add("edit-button");
                removeBtn.getStyleClass().add("remove-button");

                editBtn.setOnAction(event -> {
                    Budget budget = getTableView().getItems().get(getIndex());
                    editBudget(budget);
                });

                removeBtn.setOnAction(event -> {
                    Budget budget = getTableView().getItems().get(getIndex());
                    removeBudget(budget);
                });

                actionBox.setAlignment(Pos.CENTER);
                actionBox.getChildren().addAll(editBtn, removeBtn);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Budget budget = getTableView().getItems().get(getIndex());
                    boolean isFuture = isFutureMonth(budget.getMonth());
                    editBtn.setDisable(!isFuture);
                    removeBtn.setDisable(!isFuture);
                    setGraphic(actionBox);
                }
            }
        });
        actionCol.setPrefWidth(150);

        budgetTable.getColumns().addAll(slNoCol, categoryCol, monthCol, amountCol, actionCol);
        budgetTable.setItems(filteredBudgets);

        // Set table height
        budgetTable.setPrefHeight(200);
        budgetTable.setFixedCellSize(35);
    }

    /**
     * Sets up filtering for the budget table.
     * Filters by category, year, and month.
     */
    private void setupFiltering() {
        // Category filter
        categoryCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateMonthFilter();  // Re-filter by current year and month when category changes
        });

        // Year and month filter
        yearCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateMonthFilter());
        monthCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateMonthFilter());

        // Initially display budgets for the current year and month
        updateMonthFilter();  // Force initial filtering and refresh pie chart
    }

    /**
     * Updates the month filter for the budget table.
     * Filters budgets based on selected year, month, and category.
     */
    private void updateMonthFilter() {
        String selectedMonth = monthCombo.getValue();
        Integer selectedYear = yearCombo.getValue();
        String selectedCategory = categoryCombo.getValue();

        if (selectedMonth != null && selectedYear != null) {
            String filterMonth = selectedMonth + " " + selectedYear;

            filteredBudgets.setPredicate(budget ->
                    budget.getMonth().equals(filterMonth) &&
                            ("Select All".equals(selectedCategory) || selectedCategory == null || selectedCategory.isEmpty()
                                    || budget.getCategory().equals(selectedCategory))
            );
        } else {
            filteredBudgets.setPredicate(null);  // Show all
        }

        refreshBudgetCards();
    }

    /**
     * Checks if the given month-year string represents a future month.
     * @param monthYear The month-year string in the format "Month Year"
     * @return True if the month-year is in the future, false otherwise
     */
    private boolean isFutureMonth(String monthYear) {
        String[] parts = monthYear.split(" ");
        int monthIndex = getMonthIndex(parts[0]);
        int year = Integer.parseInt(parts[1]);

        YearMonth budgetMonth = YearMonth.of(year, monthIndex + 1);
        return !budgetMonth.isBefore(YearMonth.now());
    }

    /**
     * Gets the index of the specified month.
     * @param month The month name
     * @return The index of the month (0-based)
     */
    private int getMonthIndex(String month) {
        for (int i = 0; i < months.length; i++) {
            if (months[i].equals(month)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Handles the addition of a new budget.
     * Validates input and updates the budget set and table.
     */
    @FXML
    private void handleAddBudget() {
        String category = categoryCombo.getValue();
        double amount;
        try {
            amount = Double.parseDouble(budgetAmountField.getText());
        } catch (NumberFormatException e) {
            showAlert("Invalid Amount", "Please enter a valid number");
            return;
        }

        int year = yearCombo.getValue();
        int monthIndex = getMonthIndex(monthCombo.getValue()) + 1;
        YearMonth ym = YearMonth.of(year, monthIndex);

        // Check if budget already exists
        if (budgetSet.getBudgetsByCategory(category).containsKey(ym)) {
            showAlert("Duplicate Budget", "Budget already exists for selected category and month");
            return;
        }

        budgetSet.setBudget(category, ym, amount);
        budgets.add(new Budget( category, monthCombo.getValue() + " " + year, amount));

        refreshBudgetCards();
        budgetAmountField.clear();
    }

    /**
     * Loads budgets from the budget set into the table.
     */
    private void loadBudgetFromBudgetSet() {
        budgets.clear();

        Map<String, Map<YearMonth, Double>> allBudgets = budgetSet.getAllBudgets();

        // Expand to a list of BudgetEntry
        List<BudgetEntry> allEntries = allBudgets.entrySet().stream()
                .flatMap(entry -> entry.getValue().entrySet().stream()
                        .map(e -> new BudgetEntry(entry.getKey(), e.getKey(), e.getValue())))
                .sorted((a, b) -> {
                    int cmp = a.month.compareTo(b.month);
                    return (cmp == 0) ? a.category.compareTo(b.category) : cmp;
                })
                .collect(Collectors.toList());

        // Use Map<YearMonth, Integer> to record serial number counter per month
        Map<YearMonth, Integer> serialCounterPerMonth = new LinkedHashMap<>();

        for (BudgetEntry be : allEntries) {
            YearMonth ym = be.month;

            // Group serial number: start from 1 within each YearMonth
            int currentSerial = serialCounterPerMonth.getOrDefault(ym, 0) + 1;
            serialCounterPerMonth.put(ym, currentSerial);

            String monthStr = months[ym.getMonthValue() - 1] + " " + ym.getYear();
            budgets.add(new Budget( be.category, monthStr, be.amount));
        }

        budgetTable.refresh();
        refreshBudgetCards();
    }

    /**
     * Represents a budget entry with category, month, and amount.
     */
    private static class BudgetEntry {
        String category;
        YearMonth month;
        double amount;

        BudgetEntry(String category, YearMonth month, double amount) {
            this.category = category;
            this.month = month;
            this.amount = amount;
        }
    }

    /**
     * Edits an existing budget.
     * Prompts the user for a new amount and updates the budget set and table.
     * @param budget The budget to edit
     */
    private void editBudget(Budget budget) {
        TextInputDialog dialog = new TextInputDialog(Double.toString(budget.getAmount()));
        dialog.setTitle("Edit Budget");
        dialog.setHeaderText("Edit budget for " + budget.getCategory() + " (" + budget.getMonth() + ")");
        dialog.setContentText("New amount:");

        dialog.showAndWait().ifPresent(newAmount -> {
            try {
                double amount = Double.parseDouble(newAmount);
                budget.setAmount(amount);

                // Synchronize with BudgetSet and save
                String[] parts = budget.getMonth().split(" ");
                YearMonth ym = YearMonth.of(Integer.parseInt(parts[1]), getMonthIndex(parts[0]) + 1);
                budgetSet.setBudget(budget.getCategory(), ym, amount);

                budgetTable.refresh();
                refreshBudgetCards();
            } catch (NumberFormatException e) {
                showAlert("Invalid Amount", "Please enter a valid number");
            }
        });
    }

    /**
     * Removes an existing budget.
     * Updates the budget set and table.
     * @param budget The budget to remove
     */
    private void removeBudget(Budget budget) {
        String[] parts = budget.getMonth().split(" ");
        YearMonth ym = YearMonth.of(Integer.parseInt(parts[1]), getMonthIndex(parts[0]) + 1);

        budgetSet.removeBudget(budget.getCategory(), ym);
        budgets.remove(budget);
        refreshBudgetCards();
    }

    /**
     * Refreshes the budget cards displayed in the UI.
     */
    private void refreshBudgetCards() {
        budgetCardsContainer.getChildren().clear();

        filteredBudgets.forEach(budget -> {
            String[] parts = budget.getMonth().split(" ");
            int year = Integer.parseInt(parts[1]);
            int monthIndex = getMonthIndex(parts[0]) + 1;
            YearMonth ym = YearMonth.of(year, monthIndex);

            // Get progress
            double progress = budgetSet.getBudgetProgress(budget.getCategory(), ym);

            PieChart pieChart = createBudgetPieChart(progress, budget.getAmount());
            VBox card = createBudgetCard(budget.getCategory(), budget.getAmount(), progress, pieChart);
            budgetCardsContainer.getChildren().add(card);
        });
    }

    /**
     * Creates a pie chart for budget progress.
     * @param progress The progress percentage
     * @param totalAmount The total budget amount
     * @return A PieChart instance
     */
    private PieChart createBudgetPieChart(double progress, double totalAmount) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Used", totalAmount * progress),
                new PieChart.Data("Remaining", totalAmount * (1 - progress))
        );

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setLegendVisible(false);
        pieChart.setLabelsVisible(false);
        pieChart.setPrefSize(150, 150);

        return pieChart;
    }

    /**
     * Creates a budget card for display.
     * @param category The budget category
     * @param amount The budget amount
     * @param progress The budget progress percentage
     * @param chart The pie chart representing the budget progress
     * @return A VBox instance representing the budget card
     */
    private VBox createBudgetCard(String category, double amount, double progress, PieChart chart) {
        VBox card = new VBox(10);
        card.getStyleClass().add("budget-card");
        card.setPrefSize(180, 220);

        Label categoryLabel = new Label(category);
        categoryLabel.getStyleClass().add("card-category");

        Label amountLabel = new Label(String.format("$%.2f", amount));
        amountLabel.getStyleClass().add("card-amount");

        Label progressLabel = new Label(String.format("Progress: %.0f%%", progress * 100));
        progressLabel.getStyleClass().add("card-progress");

        card.getChildren().addAll(categoryLabel, chart, amountLabel, progressLabel);
        return card;
    }

    /**
     * Displays an alert dialog with the specified title and message.
     * @param title The alert title
     * @param message The alert message
     */
    @FXML
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private StackPane progressOverlay;

    /**
     * Handles the generation of spending insights.
     * Displays a progress indicator and performs the analysis in a background task.
     */
    @FXML
    private void handleGenerateInsights() {
        // Show progress indicator and disable button
        progressOverlay.setVisible(true);
        insightButton.setDisable(true);

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                try {
                    ExpenseManager manager = new ExpenseManager("sk-cbzpgeqjquxjgusngdklsmrzikmptukukbrvzbjhibsosfyf");
                    List<ExpenseRecord> expenseList = manager.getExpenses();
                    SpendingInsightService insightService = new SpendingInsightService(expenseList, manager.getApiClient());
                    return insightService.generateSpendingInsights();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to generate insights: " + e.getMessage(), e);
                }
            }
        };

        // Success callback
        task.setOnSucceeded(e -> {
            progressOverlay.setVisible(false);
            insightButton.setDisable(false);
            showResultDialog(task.getValue());
        });

        // Failure callback
        task.setOnFailed(e -> {
            progressOverlay.setVisible(false);
            insightButton.setDisable(false);
            showErrorDialog(task.getException());
        });

        // Start task
        new Thread(task).start();
    }

    /**
     * Displays the result of the spending insights analysis.
     * @param result The analysis result
     */
    private void showResultDialog(String result) {
        TextArea resultArea = new TextArea(result);
        resultArea.setWrapText(true);
        resultArea.setEditable(false);
        resultArea.setPrefSize(500, 400);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Spending Insights");
        alert.setHeaderText("AI Analysis Result");
        alert.getDialogPane().setContent(resultArea);
        alert.show();
    }

    /**
     * Displays an error dialog with the specified exception message.
     * @param ex The exception
     */
    private void showErrorDialog(Throwable ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation Failed");
        alert.setContentText(ex.getMessage());
        alert.show();
    }

    /**
     * Budget model class.
     */
    public static class Budget {
        private final StringProperty category;
        private final StringProperty month;
        private final DoubleProperty amount;

        public Budget(String category, String month, double amount) {
            this.category = new SimpleStringProperty(category);
            this.month = new SimpleStringProperty(month);
            this.amount = new SimpleDoubleProperty(amount);
        }

        public String getCategory() {
            return category.get();
        }

        public void setCategory(String category) {
            this.category.set(category);
        }

        public StringProperty categoryProperty() {
            return category;
        }

        public String getMonth() {
            return month.get();
        }

        public void setMonth(String month) {
            this.month.set(month);
        }

        public StringProperty monthProperty() {
            return month;
        }

        public double getAmount() {
            return amount.get();
        }

        public void setAmount(double amount) {
            this.amount.set(amount);
        }

        public DoubleProperty amountProperty() {
            return amount;
        }
    }
}

