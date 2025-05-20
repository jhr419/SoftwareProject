import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class IncomeAnalyzer {

    // 按收入分类汇总
    public static void displayIncomeByCategory(List<ExpenseRecord> records) {
        Map<String, Double> incomeTotals = new HashMap<>();
        for (ExpenseRecord record : records) {
            if(record.getTransactionType().equalsIgnoreCase("income")) {
                String category = record.getCategory();
                double amount = record.getAmount();
                incomeTotals.put(category, incomeTotals.getOrDefault(category, 0.0) + amount);
            }
        }

        System.out.println("📊 收入分类统计：");
        for (Map.Entry<String, Double> entry : incomeTotals.entrySet()) {
            System.out.println("  分类: " + entry.getKey() + " | 总收入: " + entry.getValue());
        }
    }

    // 按年统计收入
    public static void displayIncomeByYear(List<ExpenseRecord> records) {
        Map<String, Map<Integer, Double>> yearTotals = new HashMap<>();

        for (ExpenseRecord record : records) {
            if(record.getTransactionType().equalsIgnoreCase("income")) {
                String category = record.getCategory();
                int year = record.getDate().getYear();
                double amount = record.getAmount();

                yearTotals
                        .computeIfAbsent(category, k -> new HashMap<>())
                        .merge(year, amount, Double::sum);
            }
        }

        System.out.println("📅 年度收入统计：");
        for (String category : yearTotals.keySet()) {
            System.out.println("分类: " + category);
            for (Map.Entry<Integer, Double> entry : yearTotals.get(category).entrySet()) {
                System.out.println("  年: " + entry.getKey() + " | 总收入: " + entry.getValue());
            }
        }
    }

    // 按月统计收入
    public static void displayIncomeByMonth(List<ExpenseRecord> records) {
        Map<String, Map<Month, Double>> monthTotals = new HashMap<>();

        for (ExpenseRecord record : records) {
            if(record.getTransactionType().equalsIgnoreCase("income")) {
                String category = record.getCategory();
                Month month = record.getDate().getMonth();
                double amount = record.getAmount();

                monthTotals
                        .computeIfAbsent(category, k -> new HashMap<>())
                        .merge(month, amount, Double::sum);
            }
        }

        System.out.println("📆 月度收入统计：");
        for (String category : monthTotals.keySet()) {
            System.out.println("分类: " + category);
            for (Map.Entry<Month, Double> entry : monthTotals.get(category).entrySet()) {
                System.out.println("  月: " + entry.getKey() + " | 总收入: " + entry.getValue());
            }
        }
    }

    // 按日统计收入数据（返回Map，供图表使用）
    public static Map<LocalDate, Double> getDailyIncomeData(List<ExpenseRecord> records) {
        Map<LocalDate, Double> dailyData = new HashMap<>();
        for (ExpenseRecord record : records) {
            if(record.getTransactionType().equalsIgnoreCase("income")) {
                LocalDate date = record.getDate();
                dailyData.put(date, dailyData.getOrDefault(date, 0.0) + record.getAmount());
            }
        }
        return dailyData;
    }
}
