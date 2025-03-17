import java.time.LocalDate;

public class ExpenseRecord {
    private String category;  // 分类
    private double amount;    // 金额
    private LocalDate date;   // 日期
    private String itemName;  // 购买物品名称

    public ExpenseRecord(String category, double amount, LocalDate date, String itemName) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getItemName() {
        return itemName;
    }

    @Override
    public String toString() {
        return category + "," + amount + "," + date + "," + itemName;
    }
}
