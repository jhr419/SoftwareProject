import java.util.*;

public class ExpenseAnalyzer {
    private Map<String, Double> categoryExpenses;  // 用于存储每个类别的消费总额

    // 构造函数，初始化消费数据
    public ExpenseAnalyzer() {
        categoryExpenses = new HashMap<>();
    }

    // 添加支出金额
    public void addExpense(String category, double amount) {
        categoryExpenses.put(category, categoryExpenses.getOrDefault(category, 0.0) + amount); // 累加每个类别的消费金额
    }

    // 获取每个类别的消费总额
    public Map<String, Double> getCategoryExpenses() {
        return categoryExpenses;
    }

    // 获取每个类别的年度消费趋势
    public void displayAnnualTrends() {
        System.out.println("类别年度消费趋势:");
        // 遍历每个类别，打印出该类别的消费总额
        for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
            System.out.println("类别: " + entry.getKey() + " | 总消费: " + entry.getValue());
        }
    }
}
