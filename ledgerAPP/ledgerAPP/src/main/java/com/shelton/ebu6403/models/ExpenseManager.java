package com.shelton.ebu6403.models;

import java.io.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class ExpenseManager {

    private List<ExpenseRecord> expenses;
    private BudgetSet budgetSet;
    private String cla;
    // 添加AI 自动分类
    private ApiClient apiClient;
    private static final Set<String> predefinedCategories = new HashSet<>(Arrays.asList(
            "Travel", "Entertainment", "Clothing", "Education", "Transportation",
            "Medical", "Home", "Food", "Sports", "Communication", "Others"
    ));

    public ExpenseManager(String apiKey) {
        this.apiClient = new ApiClient(apiKey); // 初始化ApiClient
        expenses = new ArrayList<>();
        budgetSet = new BudgetSet();
        loadData();

        //loadExpensesFromFile();
    }
    public ApiClient getApiClient() {
        return this.apiClient;
    }

    public String classifyWithAI(String itemName, String transactionType) {
        try {
            String[] categories = transactionType.equalsIgnoreCase("income")
                    ? new String[] {"Salary", "Investment", "Gift", "Bonus","Others"}
                    : new String[] {"Travel", "Entertainment", "Clothing", "Education", "Transportation",
                    "Medical", "Home", "Food", "Sports", "Communication","others"};

            StringBuilder prompt = new StringBuilder("Classify this " + transactionType + " item: " + itemName + "\n\n");
            prompt.append("Please choose one of the following categories:\n");
            for (String category : categories) {
                prompt.append("- ").append(category).append("\n");
            }
            prompt.append("Your answer should only contain the category name, no other explanation.\n");

            String response = apiClient.sendRequest(prompt.toString());
            return response.trim();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("response error");

            return "Others";
        }
    }

    //决定写入文件类型
    private void saveToProperCSV(ExpenseRecord record) {
        String filePath = record.getTransactionType().equalsIgnoreCase("expense")
                ? "resources/expenses.csv"
                : "resources/savings.csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(record.getItemName() + "," +
                    record.getCategory() + "," +
                    record.getAmount() + "," +
                    record.getDate() + "," +
                    record.getTransactionType());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing record: " + e.getMessage());
        }
    }
    //复写整个文件
    public void saveExpensesToFile() {
        try (
                BufferedWriter expenseWriter = new BufferedWriter(new FileWriter("resources/expenses.csv"));
                BufferedWriter savingsWriter = new BufferedWriter(new FileWriter("resources/savings.csv"));
        ) {
            for (ExpenseRecord record : expenses) {
                String baseLine = record.getItemName() + "," +
                        record.getCategory() + "," +
                        record.getAmount() + "," +
                        record.getDate() + "," +
                        record.getTransactionType();

                // 按类型写入
                if (record.getTransactionType().equalsIgnoreCase("expense")) {
                    expenseWriter.write(baseLine);
                    expenseWriter.newLine();
                } else {
                    savingsWriter.write(baseLine);
                    savingsWriter.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving all expenses: " + e.getMessage());
        }
    }


    // 从CSV文件加载记录（支持收入和支出）
    public void loadExpensesFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 4) continue;

                String itemName = data[0];
                double amount = Double.parseDouble(data[1]);
                LocalDate date = LocalDate.parse(data[2]);
                String transactionType = data[3];

                String category = (data.length >= 5 && !data[4].trim().isEmpty())
                        ? data[4].trim()
                        : classifyWithAI(itemName,transactionType);

                ExpenseRecord record = new ExpenseRecord(category, amount, date, itemName, transactionType);
                expenses.add(record);
                saveToProperCSV(record);
            }
        } catch (IOException e) {
            System.out.println("Error loading expenses from " + filePath + ": " + e.getMessage());
        }
    }
    public void loadData() {
        // 加载 expenses.csv（支出记录）
        loadFromCSV("resources/expenses.csv");

        // 加载 savings.csv（收入记录）
        loadFromCSV("resources/savings.csv");
    }

    // 抽出通用 CSV 加载逻辑
    private void loadFromCSV(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 5) continue;

                String itemName = data[0];
                String category = data[1];
                double amount = Double.parseDouble(data[2]);
                LocalDate date = LocalDate.parse(data[3]);
                String transactionType = data[4];

                ExpenseRecord record = new ExpenseRecord(category, amount, date, itemName, transactionType);
                expenses.add(record);
            }
        } catch (IOException e) {
            System.out.println("Failed to load from " + filePath + ": " + e.getMessage());
        }
    }

    // 添加新的记录（交易）
    public void addExpense(String itemName, double amount, LocalDate date, String transactionType, String userCategory) {
        String finalCategory = (userCategory != null && !userCategory.isBlank())
                ? userCategory
                : classifyWithAI(itemName,transactionType);

        ExpenseRecord record = new ExpenseRecord(finalCategory, amount, date, itemName, transactionType);
        expenses.add(record);
        saveToProperCSV(record);  // 写入正确的CSV文件

        if (transactionType.equalsIgnoreCase("expense")) {
            budgetSet.addExpense(finalCategory, date, amount);
        }
    }
    public void addExpense(String itemName, double amount, LocalDate date, String transactionType) {
        addExpense(itemName, amount, date, transactionType, null);
    }

    // 显示所有记录（控制台输出）
    public void displayExpenses() {
        for (ExpenseRecord record : expenses) {
            System.out.println(record);
        }
    }

    // 按分类统计出账总额
    public void displayCategoryExpenses() {
        Map<String, Double> categoryTotals = new HashMap<>();
        for (ExpenseRecord record : expenses) {
            if(record.getTransactionType().equalsIgnoreCase("expense")) {
                String category = record.getCategory();
                double amount = record.getAmount();
                categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
            }
        }
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            System.out.println("分类: " + entry.getKey() + " | 总支出: " + entry.getValue());
        }
    }

    // 按日统计出账数据，用于折线图绘制
    public Map<LocalDate, Double> getDailySpendingData() {
        Map<LocalDate, Double> dailyData = new HashMap<>();
        for (ExpenseRecord record : expenses) {
            if(record.getTransactionType().equalsIgnoreCase("expense")) {
                LocalDate date = record.getDate();
                dailyData.put(date, dailyData.getOrDefault(date, 0.0) + record.getAmount());
            }
        }
        return dailyData;
    }

    // 新增：按日统计收入数据
    public Map<LocalDate, Double> getDailyIncomeData() {
        Map<LocalDate, Double> dailyData = new HashMap<>();
        for (ExpenseRecord record : expenses) {
            if(record.getTransactionType().equalsIgnoreCase("income")) {
                LocalDate date = record.getDate();
                dailyData.put(date, dailyData.getOrDefault(date, 0.0) + record.getAmount());
            }
        }
        return dailyData;
    }

    // 按分类统计出账数据，用于饼图绘制
    public Map<String, Double> SpendingData() {
        Map<String, Double> categoryData = new HashMap<>();
        for (ExpenseRecord record : expenses) {
            if(record.getTransactionType().equalsIgnoreCase("expense")) {
                String category = record.getCategory();
                categoryData.put(category, categoryData.getOrDefault(category, 0.0) + record.getAmount());
            }
        }
        return categoryData;
    }

    // 按分类和时间（年）统计出账总额
    public void displayCategoryAndTimeExpenses() {
        Map<String, Map<Integer, Double>> categoryTimeTotals = new HashMap<>();

        for (ExpenseRecord record : expenses) {
            if(record.getTransactionType().equalsIgnoreCase("expense")) {
                String category = record.getCategory();
                int year = record.getDate().getYear();
                double amount = record.getAmount();

                categoryTimeTotals
                        .computeIfAbsent(category, k -> new HashMap<>())
                        .merge(year, amount, Double::sum);
            }
        }

        for (Map.Entry<String, Map<Integer, Double>> entry : categoryTimeTotals.entrySet()) {
            System.out.println("分类: " + entry.getKey());
            for (Map.Entry<Integer, Double> yearEntry : entry.getValue().entrySet()) {
                System.out.println("  年: " + yearEntry.getKey() + " | 总支出: " + yearEntry.getValue());
            }
        }
    }

    // 按分类和时间（月）统计出账总额
    public void displayCategoryAndMonthExpenses() {
        Map<String, Map<Month, Double>> categoryMonthTotals = new HashMap<>();

        for (ExpenseRecord record : expenses) {
            if(record.getTransactionType().equalsIgnoreCase("expense")) {
                String category = record.getCategory();
                Month month = record.getDate().getMonth();
                double amount = record.getAmount();

                categoryMonthTotals
                        .computeIfAbsent(category, k -> new HashMap<>())
                        .merge(month, amount, Double::sum);
            }
        }

        for (Map.Entry<String, Map<Month, Double>> entry : categoryMonthTotals.entrySet()) {
            System.out.println("分类: " + entry.getKey());
            for (Map.Entry<Month, Double> monthEntry : entry.getValue().entrySet()) {
                System.out.println("  月: " + monthEntry.getKey() + " | 总支出: " + monthEntry.getValue());
            }
        }
    }

    // 按分类和日期统计出账总额
    public void displayCategoryAndDayExpenses() {
        Map<String, Map<LocalDate, Double>> categoryDayTotals = new HashMap<>();

        for (ExpenseRecord record : expenses) {
            if(record.getTransactionType().equalsIgnoreCase("expense")) {
                String category = record.getCategory();
                LocalDate date = record.getDate();
                double amount = record.getAmount();

                categoryDayTotals
                        .computeIfAbsent(category, k -> new HashMap<>())
                        .merge(date, amount, Double::sum);
            }
        }

        for (Map.Entry<String, Map<LocalDate, Double>> entry : categoryDayTotals.entrySet()) {
            System.out.println("分类: " + entry.getKey());
            for (Map.Entry<LocalDate, Double> dayEntry : entry.getValue().entrySet()) {
                System.out.println("  日期: " + dayEntry.getKey() + " | 总支出: " + dayEntry.getValue());
            }
        }
    }

    public void setBudget(String category, LocalDate endDate, double amount) {
        budgetSet.setBudget(category, endDate, amount);
    }

    // 获取预算进度
    public double getBudgetProgress(String category, LocalDate budgetSetDate, LocalDate endDate) {
        return budgetSet.getBudgetProgress(category, budgetSetDate, endDate);
    }

    // 删除预算
    public boolean removeBudget(String category, LocalDate endDate) {
        return budgetSet.removeBudget(category, endDate);
    }

    // 获取所有预算
    public Map<String, Map<LocalDate, Double>> getAllBudgets() {
        return budgetSet.getAllBudgets();
    }

    // 获取所有记录
    public List<ExpenseRecord> getExpenses() {
        return expenses;
    }

    // 修改指定索引处的记录
    public boolean updateExpense(int index, ExpenseRecord newRecord) {
        if (index >= 0 && index < expenses.size()) {
            expenses.set(index, newRecord);
            saveExpensesToFile();
            return true;
        }
        return false;
    }

    // 删除指定索引处的记录
    public boolean deleteExpense(int index) {
        if (index >= 0 && index < expenses.size()) {
            expenses.remove(index);
            saveExpensesToFile();
            return true;
        }
        return false;
    }
    public Map<String, Double> getBudgetsByDate(LocalDate date) {
        return budgetSet.getClosestBudgetBeforeDate(date);
    }
    public Map<LocalDate, Double> getBudgetsByCategory(String category) {
        return budgetSet.getBudgetsByCategory(category);
    }

    // 新增：对收入和支出进行分类统计，返回一个 Map，
    // 键为 "income" 或 "expense"，值为每个类别的总金额
    public Map<String, Map<String, Double>> getClassificationData() {
        Map<String, Map<String, Double>> classification = new HashMap<>();
        classification.put("income", new HashMap<>());
        classification.put("expense", new HashMap<>());
        for (ExpenseRecord record : expenses) {
            String type = record.getTransactionType().toLowerCase();
            String category = record.getCategory();
            Map<String, Double> typeMap = classification.get(type);
            typeMap.put(category, typeMap.getOrDefault(category, 0.0) + record.getAmount());
        }
        return classification;
    }


    public void setSavings(String category, LocalDate date, double amount) {
        budgetSet.setSavings(category, date, amount);
    }

    public Map<String, Map<LocalDate, Double>> getAllSavings() {
        return budgetSet.getAllSavings();
    }

    public Map<LocalDate, Double> getSavingsByCategory(String category) {
        return budgetSet.getSavingsByCategory(category);
    }

    public Map<String, Double> getSavingsByDate(LocalDate date) {
        return budgetSet.getSavingsByDate(date);
    }


}
