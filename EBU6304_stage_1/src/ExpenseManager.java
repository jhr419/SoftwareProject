import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.time.Month;

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
        loadExpensesFromFile();
    }
    public String classifyWithAI(String itemName) {
        try {
            // 构建请求的提示信息
            String[] categories = {"Travel", "Entertainment", "Clothing", "Education", "Transportation",
                    "Medical", "Home", "Food", "Sports", "Communication"};

            StringBuilder prompt = new StringBuilder("Classify this expense item: " + itemName + "\n\n");
            prompt.append("Please choose one of the following categories:\n");
            for (String category : categories) {
                prompt.append("- ").append(category).append("\n");
            }
            prompt.append("- Others\n");
            prompt.append("Your answer should only cover the name of classification. No other explain is needed\n");

            // 请求AI模型进行分类
            String response = apiClient.sendRequest(prompt.toString());

            // 解析AI返回的分类
            cla=extractCategoryFromResponse(response);
            System.out.println(cla);
            return cla;
        } catch (Exception e) {
            e.printStackTrace();
            return "Others"; // 默认分类为Others
        }
    }

    // 从AI响应中提取分类
    private String extractCategoryFromResponse(String response) {
        response = response.trim();  // 去除多余的空格

        // 检查返回的分类是否是预定义的分类之一
        if (predefinedCategories.contains(response)) {
            return response;
        } else {
            return "Others"; // 如果不在预定义分类中，返回Others
        }
    }


    // 从CSV文件加载记录（支持收入和支出）
    private void loadExpensesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("input.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                // 如果数据不足4项则跳过
                if(data.length < 4) continue;
                //String category = data[0];
                double amount = Double.parseDouble(data[1]);
                LocalDate date = LocalDate.parse(data[2]);
                String itemName = data[0];
                String transactionType = "expense";
                if(data.length >= 4) {
                    transactionType = data[3];
                }
                String aiCategory = classifyWithAI(itemName);
                expenses.add(new ExpenseRecord(aiCategory, amount, date, itemName, transactionType));
                saveExpensesToFile();
            }
        } catch (IOException e) {
            System.out.println("Error loading expenses: " + e.getMessage());
        }
    }


    // 保存记录到CSV文件
    public void saveExpensesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("expenses.csv"))) {
            for (ExpenseRecord record : expenses) {

                writer.write(record.getItemName() + "," +
                        record.getAiCategory() + "," +
                        record.getAmount() + "," +
                        record.getDate() + "," +
                        record.getTransactionType());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving expenses: " + e.getMessage());
        }
    }

    // 添加新的记录（交易）
    public void addExpense(String itemName, double amount, LocalDate date,  String transactionType) {
        String aiCategory = classifyWithAI(itemName);  // 使用AI分类

        ExpenseRecord record = new ExpenseRecord(aiCategory, amount, date, itemName, transactionType);
        expenses.add(record);
        saveExpensesToFile();  // 保留原有保存功能

        // 如果是支出，则更新预算数据
        if (transactionType.equalsIgnoreCase("expense")) {
            budgetSet.addExpense(aiCategory, date, amount);
        }
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
                String category = record.getAiCategory();
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
    public Map<String, Double> getAiCategorySpendingData() {
        Map<String, Double> categoryData = new HashMap<>();
        for (ExpenseRecord record : expenses) {
            if(record.getTransactionType().equalsIgnoreCase("expense")) {
                String category = record.getAiCategory();
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
                String category = record.getAiCategory();
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
                String category = record.getAiCategory();
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
                String category = record.getAiCategory();
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
        return budgetSet.getBudgetsByDate(date);
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
            String category = record.getAiCategory();
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
