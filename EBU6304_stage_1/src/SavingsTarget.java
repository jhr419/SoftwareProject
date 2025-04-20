import java.time.LocalDate;

public class SavingsTarget {
    private String name;        // 储蓄目标名称
    private String category;    // 类别
    private LocalDate startDate; // 开始日期
    private LocalDate endDate;   // 结束日期
    private double goalAmount;   // 储蓄目标金额

    // 构造函数
    public SavingsTarget(String name, String category, LocalDate startDate, LocalDate endDate, double goalAmount) {
        this.name = name;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.goalAmount = goalAmount;
    }

    // Getter 和 Setter 方法
    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getGoalAmount() {
        return goalAmount;
    }

    @Override
    public String toString() {
        return String.format("目标: %s, 类别: %s, 开始日期: %s, 结束日期: %s, 目标金额: %.2f",
                name, category, startDate, endDate, goalAmount);
    }
}