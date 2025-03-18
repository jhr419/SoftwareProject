import java.time.LocalDate;

public class ExpenseRecord {
    private String category;       // 分类
    private double amount;         // 金额
    private LocalDate date;        // 日期
    private String itemName;       // 物品名称
    private String transactionType; // 交易类型："income" 或 "expense"

    public ExpenseRecord(String category, double amount, LocalDate date, String itemName, String transactionType) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.itemName = itemName;
        this.transactionType = transactionType;
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

    public String getTransactionType() {
        return transactionType;
    }

    @Override
    public String toString() {
        // 保存时采用 CSV 格式，新增交易类型字段
        return category + "," + amount + "," + date + "," + itemName + "," + transactionType;
    }
}
