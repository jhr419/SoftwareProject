package main.java.com.shelton.ebu6403.models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
                    "Medical", "Home", "Food", "Sports", "Communication","Others"};

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

    public void loadData() {
        loadExpensesFromCSV("data/expenses.csv");
        loadIncomesFromCSV("data/incomes.csv");
    }

    // 抽出通用 CSV 加载逻辑
    private void loadCSVWithType(String filePath, String transactionType) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }

                String[] data = line.split(",", -1);
                if (data.length < 5) continue;

                String name = data[1];
                LocalDate date = LocalDate.parse(data[2]);
                double amount = Double.parseDouble(data[3]);
                String category = data[4];

                ExpenseRecord record = new ExpenseRecord(category, amount, date, name, transactionType);
                expenses.add(record);
            }
        } catch (IOException e) {
            System.out.println("Failed to load from " + filePath + ": " + e.getMessage());
        }
    }
    private void loadExpensesFromCSV(String filePath) {
        loadCSVWithType(filePath, "expense");
    }
    private void loadIncomesFromCSV(String filePath) {
        loadCSVWithType(filePath, "income");
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

    public List<ExpenseRecord> getExpenses() {
        return expenses;
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

}
