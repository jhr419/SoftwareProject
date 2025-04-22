import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class BudgetSet {
    private Map<String, Map<LocalDate, Double>> categoryBudgets;  // {类别 -> {截止日期 -> 预算金额}}
    private Map<String, Map<LocalDate, Double>> categorySpending; // {类别 -> {日期 -> 已支出金额}}
    private static final String BUDGET_FILE = "resources/budget.csv";
    private Map<String, Map<LocalDate, Double>> categorySavings;  // {类别 -> {日期 -> 储蓄金额}}
    public BudgetSet() {
        categoryBudgets = new HashMap<>();
        categorySpending = new HashMap<>();
        categorySavings = new HashMap<>();  // 初始化储蓄数据
        loadBudgetsFromFile();
        loadExpensesFromFile(); // **新增：在启动时加载 `expenses.csv`**

    }

    public void setSavings(String category, LocalDate date, double amount) {
        categorySavings.putIfAbsent(category, new HashMap<>());
        categorySavings.get(category).put(date, amount);
        saveSavingsToFile();
    }

    // **记录储蓄**
    public void addSavings(String category, LocalDate date, double amount) {
        categorySavings.putIfAbsent(category, new HashMap<>());
        categorySavings.get(category).put(date, categorySavings.get(category).getOrDefault(date, 0.0) + amount);
    }

    // **查询所有储蓄**
    public Map<String, Map<LocalDate, Double>> getAllSavings() {
        return categorySavings;
    }

    // **按类别查询储蓄**
    public Map<LocalDate, Double> getSavingsByCategory(String category) {
        return categorySavings.getOrDefault(category, new HashMap<>());
    }

    // **按日期查询储蓄**
    public Map<String, Double> getSavingsByDate(LocalDate date) {
        Map<String, Double> result = new HashMap<>();
        for (String category : categorySavings.keySet()) {
            if (categorySavings.get(category).containsKey(date)) {
                result.put(category, categorySavings.get(category).get(date));
            }
        }
        return result;
    }

    private void saveSavingsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/savings.csv"))) {
            for (String category : categorySavings.keySet()) {
                for (LocalDate date : categorySavings.get(category).keySet()) {
                    writer.write(category + "," + date + "," + categorySavings.get(category).get(date));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving savings data: " + e.getMessage());
        }
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

     //**按截止日期查询预算**
    public Map<String, Double> getBudgetsByDate(LocalDate endDate) {
        Map<String, Double> result = new HashMap<>();
        for (String category : categoryBudgets.keySet()) {
            if (categoryBudgets.get(category).containsKey(endDate)) {
                result.put(category, categoryBudgets.get(category).get(endDate));
            }
        }
        return result;
    }
    // **按日期查询离用户输入日期最近的预算（截止日期必须在输入日期之前）**
    public Map<String, Double> getClosestBudgetBeforeDate(LocalDate inputDate) {
        // 创建一个Map来保存所有符合条件的预算
        Map<String, Double> closestBudget = new HashMap<>();

        // 存储离用户输入日期最近的预算
        LocalDate closestDate = null;
        double closestBudgetAmount = 0.0;

        // 遍历所有预算，找到截止日期在用户输入日期之前且最接近的那个预算
        for (String category : categoryBudgets.keySet()) {
            for (Map.Entry<LocalDate, Double> entry : categoryBudgets.get(category).entrySet()) {
                LocalDate budgetEndDate = entry.getKey();
                double budgetAmount = entry.getValue();

                // 只考虑截止日期在用户输入日期之前的预算
                if (budgetEndDate.isBefore(inputDate)) {
                    // 查找最接近的截止日期
                    if (closestDate == null || budgetEndDate.isAfter(closestDate)) {
                        closestDate = budgetEndDate;
                        closestBudgetAmount = budgetAmount;
                    }
                }
            }
        }

        // 如果找到了最接近的预算，返回它
        if (closestDate != null) {
            closestBudget.put(closestDate.toString(), closestBudgetAmount);
        }

        return closestBudget;
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
    public double getBudgetProgress(String category, LocalDate inputStartDate, LocalDate inputEndDate) {
        // 1. 计算用户输入时间范围内的支出总金额
        double totalSpent = 0.0;

        // 遍历支出数据，计算在用户输入日期范围内的支出金额
        for (Map.Entry<LocalDate, Double> entry : categorySpending.getOrDefault(category, new HashMap<>()).entrySet()) {
            LocalDate expenseDate = entry.getKey();
            if (!expenseDate.isBefore(inputStartDate) && !expenseDate.isAfter(inputEndDate)) {
                totalSpent += entry.getValue();  // 累计在用户输入的日期范围内的支出
            }
        }

        // 2. 获取所有预算，并根据预算的截止日期过滤（截止日期必须大于用户输入的结束日期）
        Map<LocalDate, Double> relevantBudgets = categoryBudgets.getOrDefault(category, new HashMap<>())
                .entrySet().stream()
                .filter(entry -> !entry.getKey().isBefore(inputEndDate))  // 过滤出截止日期晚于用户输入的预算
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // 3. 如果没有符合条件的预算，返回0
        if (relevantBudgets.isEmpty()) {
            return 0.0;
        }

        // 4. 计算进度并返回进度百分比
        double totalBudget = 0.0;
        double totalSpentForProgress = 0.0;

        for (Map.Entry<LocalDate, Double> budgetEntry : relevantBudgets.entrySet()) {
            LocalDate budgetEndDate = budgetEntry.getKey();
            double budgetAmount = budgetEntry.getValue();

            // 计算该预算的进度
            double progress = Math.min(1.0, totalSpent / budgetAmount);  // 进度最大为100%

            // 累加预算总金额和支出总金额（用于显示进度）
            totalBudget += budgetAmount;
            totalSpentForProgress += totalSpent;

            // 打印每个预算的进度
            //System.out.println("Budget for " + category + " (End Date: " + budgetEndDate + "): " + (progress * 100) + "%");
        }

        // 返回计算的进度，进度最大为100%
        return totalSpentForProgress / totalBudget;
    }


    private void loadExpensesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/expenses.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 3) continue;
                String category = data[1];
                double amount = Double.parseDouble(data[2]);
                LocalDate date = LocalDate.parse(data[3]);

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