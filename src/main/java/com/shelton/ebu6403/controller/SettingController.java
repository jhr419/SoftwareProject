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

public class SettingController {
    // 顶部选项卡
    @FXML private TabPane settingsTabPane;

    // 预算设置组件
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

    @FXML
    public void initialize() {
        initSettingsTabs();
        initBudgetSettings();
        categoryCombo.setValue("Select All");
        updateMonthFilter();
        // 注册消费变化监听
        budgetSet.setOnExpenseChanged(() -> {
            refreshBudgetCards();
            budgetTable.refresh();
        });
        // 添加：启动时展示节日前提醒（非弹窗）
        ChineseHolidayAnalyzer analyzer = new ChineseHolidayAnalyzer(expenseManager.getExpenses());
        String reminder = analyzer.getUpcomingHolidayReminder();
        if (!reminder.isBlank()) {
            holidayReminderLabel.setText("⚠ " + reminder);
            holidayReminderLabel.setVisible(true);
        }
    }


    private void initSettingsTabs() {
        // 添加设置选项选项卡
        settingsTabPane.getTabs().addAll(
                createTab("Edit Account"),
                createTab("Budget Settings"),
                createTab("Security"),
                createTab("General"),
                createTab("Help")
        );
    }

    private Tab createTab(String text) {
        Tab tab = new Tab(text);
        tab.setClosable(false);
        return tab;
    }

    private void initBudgetSettings() {
        // 初始化年份选择 (当前年份+未来5年)
        int currentYear = LocalDate.now().getYear();
        yearCombo.setItems(FXCollections.observableArrayList(
                currentYear, currentYear + 1, currentYear + 2,
                currentYear + 3, currentYear + 4, currentYear + 5
        ));
        yearCombo.getSelectionModel().select(0);

        // 初始化月份选择
        monthCombo.setItems(FXCollections.observableArrayList(months));
        monthCombo.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);

        // 初始化分类选择
        List<String> allCats = new ArrayList<>(List.of(
                "Travel", "Entertainment", "Clothing", "Education", "Transportation",
                "Medical", "Home", "Food", "Sports", "Communication", "Others"
        ));
        allCats.addAll(readCustomCategories(customExpenseFile)); // 添加自定义分类
        ObservableList<String> categoryOptions = FXCollections.observableArrayList(allCats);
        categoryOptions.add(0, "Select All"); // 添加“Select All”选项
        categoryCombo.setItems(categoryOptions);
        categoryCombo.getSelectionModel().selectFirst(); // 默认选择“Select All”


        // 初始化预算表格
        initBudgetTable();

        // 加载已有数据
        loadBudgetFromBudgetSet();

        // 初始化筛选功能
        setupFiltering();
    }

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


    private void initBudgetTable() {
        // 清除旧列
        budgetTable.getColumns().clear();

        // SL No列
        TableColumn<Budget, Number> slNoCol = new TableColumn<>("SL No");
        slNoCol.setCellValueFactory(col -> new ReadOnlyIntegerWrapper(filteredBudgets.indexOf(col.getValue()) + 1));

        slNoCol.setPrefWidth(60);

        // Category列
        TableColumn<Budget, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        categoryCol.setPrefWidth(120);

        // Month列
        TableColumn<Budget, String> monthCol = new TableColumn<>("Month");
        monthCol.setCellValueFactory(cellData -> cellData.getValue().monthProperty());
        monthCol.setPrefWidth(150);

        // Budget列
        TableColumn<Budget, Double> amountCol = new TableColumn<>("Budget");
        amountCol.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
        amountCol.setPrefWidth(100);

        // Action列
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

        // 设置表格高度
        budgetTable.setPrefHeight(200);
        budgetTable.setFixedCellSize(35);
    }

    private void setupFiltering() {
        // 分类筛选
        categoryCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateMonthFilter();  // 分类改变时重新用当前年月过滤
        });

        // 年月筛选
        yearCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateMonthFilter());
        monthCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateMonthFilter());

        // 初始显示当前年月的预算
        updateMonthFilter();  // 强制初次过滤并刷新饼图
    }


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
            filteredBudgets.setPredicate(null);  // 全部显示
        }

        refreshBudgetCards();
    }



    private boolean isFutureMonth(String monthYear) {
        String[] parts = monthYear.split(" ");
        int monthIndex = getMonthIndex(parts[0]);
        int year = Integer.parseInt(parts[1]);

        YearMonth budgetMonth = YearMonth.of(year, monthIndex + 1);
        return !budgetMonth.isBefore(YearMonth.now());
    }

    private int getMonthIndex(String month) {
        for (int i = 0; i < months.length; i++) {
            if (months[i].equals(month)) {
                return i;
            }
        }
        return 0;
    }


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

        // 检查是否已存在预算
        if (budgetSet.getBudgetsByCategory(category).containsKey(ym)) {
            showAlert("Duplicate Budget", "Budget already exists for selected category and month");
            return;
        }

        budgetSet.setBudget(category, ym, amount);
        budgets.add(new Budget( category, monthCombo.getValue() + " " + year, amount));

        refreshBudgetCards();
        budgetAmountField.clear();
    }

    private void loadBudgetFromBudgetSet() {
        budgets.clear();

        Map<String, Map<YearMonth, Double>> allBudgets = budgetSet.getAllBudgets();

        // 先展开为一个 BudgetEntry 列表
        List<BudgetEntry> allEntries = allBudgets.entrySet().stream()
                .flatMap(entry -> entry.getValue().entrySet().stream()
                        .map(e -> new BudgetEntry(entry.getKey(), e.getKey(), e.getValue())))
                .sorted((a, b) -> {
                    int cmp = a.month.compareTo(b.month);
                    return (cmp == 0) ? a.category.compareTo(b.category) : cmp;
                })
                .collect(Collectors.toList());

        // 用 Map<YearMonth, Integer> 单独记录每月编号计数器
        Map<YearMonth, Integer> serialCounterPerMonth = new LinkedHashMap<>();

        for (BudgetEntry be : allEntries) {
            YearMonth ym = be.month;

            // 分组编号：每个 YearMonth 内从 1 开始编号
            int currentSerial = serialCounterPerMonth.getOrDefault(ym, 0) + 1;
            serialCounterPerMonth.put(ym, currentSerial);

            String monthStr = months[ym.getMonthValue() - 1] + " " + ym.getYear();
            budgets.add(new Budget( be.category, monthStr, be.amount));
        }

        budgetTable.refresh();
        refreshBudgetCards();
    }




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



    private void editBudget(Budget budget) {
        TextInputDialog dialog = new TextInputDialog(Double.toString(budget.getAmount()));
        dialog.setTitle("Edit Budget");
        dialog.setHeaderText("Edit budget for " + budget.getCategory() + " (" + budget.getMonth() + ")");
        dialog.setContentText("New amount:");

        dialog.showAndWait().ifPresent(newAmount -> {
            try {
                double amount = Double.parseDouble(newAmount);
                budget.setAmount(amount);

                // 同步更新 BudgetSet 并保存
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


    private void removeBudget(Budget budget) {
        String[] parts = budget.getMonth().split(" ");
        YearMonth ym = YearMonth.of(Integer.parseInt(parts[1]), getMonthIndex(parts[0]) + 1);

        budgetSet.removeBudget(budget.getCategory(), ym);
        budgets.remove(budget);
        refreshBudgetCards();
    }


    private void refreshBudgetCards() {
        budgetCardsContainer.getChildren().clear();

        filteredBudgets.forEach(budget -> {
            String[] parts = budget.getMonth().split(" ");
            int year = Integer.parseInt(parts[1]);
            int monthIndex = getMonthIndex(parts[0]) + 1;
            YearMonth ym = YearMonth.of(year, monthIndex);

            // 获取进度
            double progress = budgetSet.getBudgetProgress(budget.getCategory(), ym);

            PieChart pieChart = createBudgetPieChart(progress, budget.getAmount());
            VBox card = createBudgetCard(budget.getCategory(), budget.getAmount(), progress, pieChart);
            budgetCardsContainer.getChildren().add(card);
        });
    }

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

    @FXML
    private void handleGenerateInsights() {
        // 显示进度条并禁用按钮
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

        // 成功回调
        task.setOnSucceeded(e -> {
            progressOverlay.setVisible(false);
            insightButton.setDisable(false);
            showResultDialog(task.getValue());
        });

        // 失败回调
        task.setOnFailed(e -> {
            progressOverlay.setVisible(false);
            insightButton.setDisable(false);
            showErrorDialog(task.getException());
        });

        // 启动任务
        new Thread(task).start();
    }

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

    private void showErrorDialog(Throwable ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation Failed");
        alert.setContentText(ex.getMessage());
        alert.show();
    }


    // Budget模型类
    public static class Budget {
        //private final IntegerProperty serialNo;
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