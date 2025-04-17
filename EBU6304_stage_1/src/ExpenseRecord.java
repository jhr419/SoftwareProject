import java.time.LocalDate;

public class ExpenseRecord {
    private String category;       // 分类，例如“餐饮”、“交通”等
    private double amount;         // 金额，表示消费或收入的金额
    private LocalDate date;        // 日期，记录消费或收入的日期
    private String itemName;       // 物品名称，记录消费或收入的项目名称
    private String transactionType; // 交易类型，标识交易是“收入”还是“支出”

    // 构造函数，用于初始化一个消费记录对象
    public ExpenseRecord(String category, double amount, LocalDate date, String itemName, String transactionType) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.itemName = itemName;
        this.transactionType = transactionType;
    }

    // 获取分类
    public String getCategory() {
        return category;
    }

    // 获取金额
    public double getAmount() {
        return amount;
    }

    // 获取日期
    public LocalDate getDate() {
        return date;
    }

    // 获取物品名称
    public String getItemName() {
        return itemName;
    }

    // 获取交易类型
    public String getTransactionType() {
        return transactionType;
    }

    // 重写toString方法，将记录转换为CSV格式的字符串（便于保存到文件）
    @Override
    public String toString() {
        // 保存时采用 CSV 格式，新增交易类型字段
        return category + "," + amount + "," + date + "," + itemName + "," + transactionType;
    }
}
