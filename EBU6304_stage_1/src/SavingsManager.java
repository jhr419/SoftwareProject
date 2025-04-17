import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class SavingsManager {
    private Map<String, Map<LocalDate, Double>> categorySavings;  // 存储储蓄数据：{类别 -> {日期 -> 储蓄金额}}

    public SavingsManager() {
        categorySavings = new HashMap<>();
    }

    // 设置储蓄：为指定类别和日期设置储蓄金额
    public void setSavings(String category, LocalDate date, double amount) {
        categorySavings.putIfAbsent(category, new HashMap<>());
        categorySavings.get(category).put(date, amount);
    }

    // 增加储蓄：在指定类别和日期的储蓄基础上增加金额
    public void addSavings(String category, LocalDate date, double amount) {
        categorySavings.putIfAbsent(category, new HashMap<>());
        categorySavings.get(category).put(date, categorySavings.get(category).getOrDefault(date, 0.0) + amount);
    }

    // 获取指定类别的所有储蓄
    public Map<LocalDate, Double> getSavingsByCategory(String category) {
        return categorySavings.getOrDefault(category, new HashMap<>());
    }

    // 获取所有类别的储蓄数据
    public Map<String, Map<LocalDate, Double>> getAllSavings() {
        return categorySavings;
    }

    // 获取指定日期的所有储蓄数据
    public Map<String, Double> getSavingsByDate(LocalDate date) {
        Map<String, Double> result = new HashMap<>();
        for (String category : categorySavings.keySet()) {
            if (categorySavings.get(category).containsKey(date)) {
                result.put(category, categorySavings.get(category).get(date));
            }
        }
        return result;
    }
}
