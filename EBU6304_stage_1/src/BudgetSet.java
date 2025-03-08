import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class BudgetSet {
    private Map<String, Map<LocalDate, Double>> categoryBudgets;  // {类别 -> {截止日期 -> 预算金额}}
    private Map<String, Map<LocalDate, Double>> categorySpending; // {类别 -> {日期 -> 已支出金额}}
    private static final String BUDGET_FILE = "budget.csv";

    public BudgetSet() {
        categoryBudgets = new HashMap<>();
        categorySpending = new HashMap<>();
        loadBudgetsFromFile();
        loadExpensesFromFile(); // **新增：在启动时加载 `expenses.csv`**

    }

    // **设定预算**
    public void setBudget(String category, LocalDate endDate, double amount) {
        categoryBudgets.putIfAbsent(category, new HashMap<>());
        categoryBudgets.get(category).put(endDate, amount);
        saveBudgetsToFile();
    }

    // **记录支出**
    public void addExpense(String category, LocalDate date, double amount) {
        categorySpending.putIfAbsent(category, new HashMap<>());
        categorySpending.get(category).put(date, categorySpending.get(category).getOrDefault(date, 0.0) + amount);
    }

    // **查询所有预算**
    public Map<String, Map<LocalDate, Double>> getAllBudgets() {
        return categoryBudgets;
    }

    // **按类别查询预算**
    public Map<LocalDate, Double> getBudgetsByCategory(String category) {
        return categoryBudgets.getOrDefault(category, new HashMap<>());
    }

    // **按截止日期查询预算**
    public Map<String, Double> getBudgetsByDate(LocalDate endDate) {
        Map<String, Double> result = new HashMap<>();
        for (String category : categoryBudgets.keySet()) {
            if (categoryBudgets.get(category).containsKey(endDate)) {
                result.put(category, categoryBudgets.get(category).get(endDate));
            }
        }
        return result;
    }

    // **删除预算（优化交互）**
    public boolean removeBudget(String category, LocalDate endDate) {
        if (categoryBudgets.containsKey(category) && categoryBudgets.get(category).containsKey(endDate)) {
            categoryBudgets.get(category).remove(endDate);
            if (categoryBudgets.get(category).isEmpty()) {
                categoryBudgets.remove(category);
            }
            saveBudgetsToFile();
            return true; // 删除成功
        }
        return false; // 预算不存在
    }

    // **获取预算进度**
    public double getBudgetProgress(String category, LocalDate budgetSetDate, LocalDate endDate) {
        // 获取预算金额
        double budget = categoryBudgets.getOrDefault(category, new HashMap<>()).getOrDefault(endDate, 0.0);
        if (budget == 0) return 0.0; // 避免除零错误

        double spent = 0.0;

        // 遍历 categorySpending（包括从 expenses.csv 加载的历史消费）
        for (Map.Entry<LocalDate, Double> entry : categorySpending.getOrDefault(category, new HashMap<>()).entrySet()) {
            LocalDate expenseDate = entry.getKey();

            // **计算 budgetSetDate ~ endDate 之间的消费**
            if (!expenseDate.isBefore(budgetSetDate) && !expenseDate.isAfter(endDate)) {
                spent += entry.getValue();
            }
        }

        // 计算进度：支出金额 / 预算金额
        return Math.min(1.0, spent / budget); // **进度最大为100%**
    }

    private void loadExpensesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("expenses.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 3) continue;
                String category = data[0];
                double amount = Double.parseDouble(data[1]);
                LocalDate date = LocalDate.parse(data[2]);

                // **将历史消费数据存入 categorySpending**
                categorySpending.putIfAbsent(category, new HashMap<>());
                categorySpending.get(category).put(date, categorySpending.get(category).getOrDefault(date, 0.0) + amount);
            }
        } catch (IOException e) {
            System.out.println("No previous expense data found.");
        }
    }


    // **加载预算**
    private void loadBudgetsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BUDGET_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 3) continue;
                String category = data[0];
                String dateStr = data[1];

                // **处理 YYYY-MM 格式的日期**
                if (dateStr.length() == 7) {
                    dateStr += "-01";  // 补充为 YYYY-MM-01
                }

                LocalDate endDate = LocalDate.parse(dateStr);
                double amount = Double.parseDouble(data[2]);

                setBudget(category, endDate, amount);
            }
        } catch (IOException e) {
            System.out.println("No previous budget data found.");
        }
    }

    // **保存预算**
    private void saveBudgetsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BUDGET_FILE))) {
            for (String category : categoryBudgets.keySet()) {
                for (LocalDate endDate : categoryBudgets.get(category).keySet()) {
                    writer.write(category + "," + endDate + "," + categoryBudgets.get(category).get(endDate));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving budget data: " + e.getMessage());
        }
    }
}
