import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.time.Month;

public class ExpenseManager {
    private List<ExpenseRecord> expenses;
    private BudgetSet budgetSet;
    public ExpenseManager() {
        expenses = new ArrayList<>();
        budgetSet = new BudgetSet();
        loadExpensesFromFile();
    }

    // 从CSV文件加载支出记录
    private void loadExpensesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("expenses.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                // 保证数据完整性
                if(data.length < 4) continue;
                String category = data[0];
                double amount = Double.parseDouble(data[1]);
                LocalDate date = LocalDate.parse(data[2]);
                String itemName = data[3];
                expenses.add(new ExpenseRecord(category, amount, date, itemName));
            }
        } catch (IOException e) {
            System.out.println("Error loading expenses: " + e.getMessage());
        }
    }

    // 保存支出记录到CSV文件
    public void saveExpensesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("expenses.csv"))) {
            for (ExpenseRecord record : expenses) {
                writer.write(record.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving expenses: " + e.getMessage());
        }
    }

    // 添加新的支出记录
    public void addExpense(String category, double amount, LocalDate date, String itemName) {
        ExpenseRecord record = new ExpenseRecord(category, amount, date, itemName);
        expenses.add(record);
        saveExpensesToFile();

        // **确保 `BudgetSet` 记录消费**
        budgetSet.addExpense(category, date, amount);
    }


    // 显示所有支出记录（控制台输出）
    public void displayExpenses() {
        for (ExpenseRecord record : expenses) {
            System.out.println(record);
        }
    }

    // 按消费种类统计支出总额
    public void displayCategoryExpenses() {
        Map<String, Double> categoryTotals = new HashMap<>();
        for (ExpenseRecord record : expenses) {
            String category = record.getCategory();
            double amount = record.getAmount();
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
        }
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            System.out.println("分类: " + entry.getKey() + " | 总支出: " + entry.getValue());
        }
    }

    // 按日统计支出数据，用于折线图绘制
    public Map<LocalDate, Double> getDailySpendingData() {
        Map<LocalDate, Double> dailyData = new HashMap<>();
        for (ExpenseRecord record : expenses) {
            LocalDate date = record.getDate();
            dailyData.put(date, dailyData.getOrDefault(date, 0.0) + record.getAmount());
        }
        return dailyData;
    }

    // 按消费种类统计支出数据，用于饼图绘制
    public Map<String, Double> getCategorySpendingData() {
        Map<String, Double> categoryData = new HashMap<>();
        for (ExpenseRecord record : expenses) {
            String category = record.getCategory();
            categoryData.put(category, categoryData.getOrDefault(category, 0.0) + record.getAmount());
        }
        return categoryData;
    }

    // 按消费种类和时间（按年）分类并计算每个分类的总支出
    public void displayCategoryAndTimeExpenses() {
        Map<String, Map<Integer, Double>> categoryTimeTotals = new HashMap<>();

        for (ExpenseRecord record : expenses) {
            String category = record.getCategory();
            int year = record.getDate().getYear();
            double amount = record.getAmount();

            categoryTimeTotals
                    .computeIfAbsent(category, k -> new HashMap<>())
                    .merge(year, amount, Double::sum);
        }

        // 输出按时间（年）和分类的总支出
        for (Map.Entry<String, Map<Integer, Double>> entry : categoryTimeTotals.entrySet()) {
            System.out.println("分类: " + entry.getKey());
            for (Map.Entry<Integer, Double> yearEntry : entry.getValue().entrySet()) {
                System.out.println("  年: " + yearEntry.getKey() + " | 总支出: " + yearEntry.getValue());
            }
        }
    }

    // 按消费种类和时间（按月）分类并计算每个分类的总支出
    public void displayCategoryAndMonthExpenses() {
        Map<String, Map<Month, Double>> categoryMonthTotals = new HashMap<>();

        for (ExpenseRecord record : expenses) {
            String category = record.getCategory();
            Month month = record.getDate().getMonth();
            double amount = record.getAmount();

            categoryMonthTotals
                    .computeIfAbsent(category, k -> new HashMap<>())
                    .merge(month, amount, Double::sum);
        }

        // 输出按时间（按月）和分类的总支出
        for (Map.Entry<String, Map<Month, Double>> entry : categoryMonthTotals.entrySet()) {
            System.out.println("分类: " + entry.getKey());
            for (Map.Entry<Month, Double> monthEntry : entry.getValue().entrySet()) {
                System.out.println("  月: " + monthEntry.getKey() + " | 总支出: " + monthEntry.getValue());
            }
        }
    }

    // 按消费种类和时间（按日）分类并计算每个分类的总支出
    public void displayCategoryAndDayExpenses() {
        Map<String, Map<LocalDate, Double>> categoryDayTotals = new HashMap<>();

        for (ExpenseRecord record : expenses) {
            String category = record.getCategory();
            LocalDate date = record.getDate();
            double amount = record.getAmount();

            categoryDayTotals
                    .computeIfAbsent(category, k -> new HashMap<>())
                    .merge(date, amount, Double::sum);
        }

        // 输出按时间（按日）和分类的总支出
        for (Map.Entry<String, Map<LocalDate, Double>> entry : categoryDayTotals.entrySet()) {
            System.out.println("分类: " + entry.getKey());
            for (Map.Entry<LocalDate, Double> dayEntry : entry.getValue().entrySet()) {
                System.out.println("  日期: " + dayEntry.getKey() + " | 总支出: " + dayEntry.getValue());
            }
        }
    }
    public void setBudget(String category, LocalDate budgetSetDate, LocalDate endDate, double amount) {
        budgetSet.setBudget(category, endDate, amount);
    }


    // **获取预算进度**
    public double getBudgetProgress(String category, LocalDate budgetSetDate, LocalDate endDate) {
        return budgetSet.getBudgetProgress(category, budgetSetDate, endDate);
    }

    // **删除预算**
    public boolean removeBudget(String category, LocalDate endDate) {
        return budgetSet.removeBudget(category, endDate);
    }

    // **获取所有预算**
    public Map<String, Map<LocalDate, Double>> getAllBudgets() {
        return budgetSet.getAllBudgets();
    }
}
