import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.time.Month;

public class ExpenseManager {
    private List<ExpenseRecord> expenses;  // 用于存储所有的消费记录
    private BudgetSet budgetSet;  // 用于管理预算

    // 构造函数，初始化消费记录和预算管理
    public ExpenseManager() {
        expenses = new ArrayList<>();
        budgetSet = new BudgetSet();
        loadExpensesFromFile();  // 从文件加载消费记录
    }

    // 从CSV文件加载消费记录（支持收入和支出）
    private void loadExpensesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("expenses.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                // 如果数据不足4项则跳过
                if(data.length < 4) continue;
                String category = data[0];
                double amount = Double.parseDouble(data[1]);
                LocalDate date = LocalDate.parse(data[2]);
                String itemName = data[3];
                String transactionType = "expense";
                if(data.length >= 5) {
                    transactionType = data[4];
                }
                expenses.add(new ExpenseRecord(category, amount, date, itemName, transactionType));
            }
        } catch (IOException e) {
            System.out.println("加载消费记录时出错: " + e.getMessage());
        }
    }

    // 保存消费记录到CSV文件
    public void saveExpensesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("expenses.csv"))) {
            for (ExpenseRecord record : expenses) {
                writer.write(record.toString());  // 将每条记录写入文件
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("保存消费记录时出错: " + e.getMessage());
        }
    }

    // 添加新的消费记录（交易）
    public void addExpense(String category, double amount, LocalDate date, String itemName, String transactionType) {
        ExpenseRecord record = new ExpenseRecord(category, amount, date, itemName, transactionType);
        expenses.add(record);
        saveExpensesToFile();  // 保存新的消费记录到文件

        // 如果是支出，更新预算数据
        if (transactionType.equalsIgnoreCase("expense")) {
            budgetSet.addExpense(category, date, amount);
        }
    }

    // 显示所有消费记录（控制台输出）
    public void displayExpenses() {
        for (ExpenseRecord record : expenses) {
            System.out.println(record);
        }
    }

    // 按类别统计支出总额
    public void displayCategoryExpenses() {
        Map<String, Double> categoryTotals = new HashMap<>();
        for (ExpenseRecord record : expenses) {
            if(record.getTransactionType().equalsIgnoreCase("expense")) {
                String category = record.getCategory();
                double amount = record.getAmount();
                categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
            }
        }
        // 打印每个类别的支出总额
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            System.out.println("分类: " + entry.getKey() + " | 总支出: " + entry.getValue());
        }
    }

    // 按日统计支出数据，用于折线图绘制
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

    // 按分类统计支出数据，用于饼图绘制
    public Map<String, Double> getCategorySpendingData() {
        Map<String, Double> categoryData = new HashMap<>();
        for (ExpenseRecord record : expenses) {
            if(record.getTransactionType().equalsIgnoreCase("expense")) {
                String category = record.getCategory();
                categoryData.put(category, categoryData.getOrDefault(category, 0.0) + record.getAmount());
            }
        }
        return categoryData;
    }

    // 按分类和时间（年）统计支出总额
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

        // 打印每个类别按年统计的支出
        for (Map.Entry<String, Map<Integer, Double>> entry : categoryTimeTotals.entrySet()) {
            System.out.println("分类: " + entry.getKey());
            for (Map.Entry<Integer, Double> yearEntry : entry.getValue().entrySet()) {
                System.out.println("  年: " + yearEntry.getKey() + " | 总支出: " + yearEntry.getValue());
            }
        }
    }

    // 按分类和时间（月）统计支出总额
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

        // 打印每个类别按月统计的支出
        for (Map.Entry<String, Map<Month, Double>> entry : categoryMonthTotals.entrySet()) {
            System.out.println("分类: " + entry.getKey());
            for (Map.Entry<Month, Double> monthEntry : entry.getValue().entrySet()) {
                System.out.println("  月: " + monthEntry.getKey() + " | 总支出: " + monthEntry.getValue());
            }
        }
    }

    // 按分类和日期统计支出总额
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

        // 打印每个类别按日期统计的支出
        for (Map.Entry<String, Map<LocalDate, Double>> entry : categoryDayTotals.entrySet()) {
            System.out.println("分类: " + entry.getKey());
            for (Map.Entry<LocalDate, Double> dayEntry : entry.getValue().entrySet()) {
                System.out.println("  日期: " + dayEntry.getKey() + " | 总支出: " + dayEntry.getValue());
            }
        }
    }

    // 设置预算
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

    // 获取所有消费记录
    public List<ExpenseRecord> getExpenses() {
        return expenses;
    }

    // 修改指定索引处的消费记录
    public boolean updateExpense(int index, ExpenseRecord newRecord) {
        if (index >= 0 && index < expenses.size()) {
            expenses.set(index, newRecord);
            saveExpensesToFile();  // 保存更新后的消费记录
            return true;
        }
        return false;
    }

    // 删除指定索引处的消费记录
    public boolean deleteExpense(int index) {
        if (index >= 0 && index < expenses.size()) {
            expenses.remove(index);  // 删除消费记录
            saveExpensesToFile();  // 保存删除后的消费记录
            return true;
        }
        return false;
    }

    // 获取按日期查询的预算数据
    public Map<String, Double> getBudgetsByDate(LocalDate date) {
        return budgetSet.getBudgetsByDate(date);
    }

    // 获取按类别查询的预算数据
    public Map<LocalDate, Double> getBudgetsByCategory(String category) {
        return budgetSet.getBudgetsByCategory(category);
    }

    // 分类统计收入和支出，返回一个分类数据的Map
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

    // 设置储蓄
    public void setSavings(String category, LocalDate date, double amount) {
        budgetSet.setSavings(category, date, amount);
    }

    // 获取所有储蓄数据
    public Map<String, Map<LocalDate, Double>> getAllSavings() {
        return budgetSet.getAllSavings();
    }

    // 获取按类别查询的储蓄数据
    public Map<LocalDate, Double> getSavingsByCategory(String category) {
        return budgetSet.getSavingsByCategory(category);
    }

    // 获取按日期查询的储蓄数据
    public Map<String, Double> getSavingsByDate(LocalDate date) {
        return budgetSet.getSavingsByDate(date);
    }
}
