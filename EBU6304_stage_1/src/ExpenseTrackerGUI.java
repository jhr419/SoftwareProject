import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.Map;

// JFreeChart相关导入
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Day;

public class ExpenseTrackerGUI {
    private JFrame frame;
    private ExpenseManager expenseManager;

    public ExpenseTrackerGUI() {
        expenseManager = new ExpenseManager();
        initialize();
    }

    private void initialize() {
        // 创建主框架
        frame = new JFrame("Expense Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);  // 设置框架大小

        // 设置主面板
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());  // 使用GridBagLayout管理布局
        frame.getContentPane().add(panel, BorderLayout.CENTER);

// 完整路径到图片文件
        String imagePath = "AI_assistant.jpg";  // 你的图片路径

// 创建ImageIcon并加载图片
        ImageIcon imageIcon = new ImageIcon(imagePath);

// 获取原始图片
        Image image = imageIcon.getImage();

// 调整图片大小，比如设置宽度为200px，高度为200px
        Image resizedImage = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);  // 使用平滑缩放

// 创建新的ImageIcon对象并将调整大小后的图片设置为图标
        ImageIcon resizedImageIcon = new ImageIcon(resizedImage);

// 创建JLabel并设置ImageIcon
        JLabel imageLabel = new JLabel(resizedImageIcon);

// 设置GridBagConstraints来定位图片
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;   // 在第一列
        gbc.gridy = 0;   // 在第一行
        gbc.gridwidth = 2;  // 占用两列空间
        panel.add(imageLabel, gbc);  // 将图片添加到面板


        gbc.insets = new Insets(10, 10, 10, 10);  // 添加间距，避免按钮太挤

        // 按钮样式
        Font buttonFont = new Font("Arial", Font.PLAIN, 14);

        // 添加支出记录按钮
        JButton addExpenseButton = new JButton("Add Expense");
        addExpenseButton.setFont(buttonFont);
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2; // 按钮跨越两列
        panel.add(addExpenseButton, gbc);
        addExpenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String category = JOptionPane.showInputDialog("Enter category:");
                String amountStr = JOptionPane.showInputDialog("Enter amount:");
                String dateStr = JOptionPane.showInputDialog("Enter date (YYYY-MM-DD):");
                String itemName = JOptionPane.showInputDialog("Enter item name:");
                LocalDate date = LocalDate.parse(dateStr);
                double amount = Double.parseDouble(amountStr);
                expenseManager.addExpense(category, amount, date, itemName);
            }
        });

        // 显示所有支出记录按钮
        JButton displayExpensesButton = new JButton("Display Expenses");
        displayExpensesButton.setFont(buttonFont);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(displayExpensesButton, gbc);
        displayExpensesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                expenseManager.displayExpenses();
            }
        });

        // 显示按分类统计的支出记录
        JButton displayCategoryButton = new JButton("Display Category Expenses");
        displayCategoryButton.setFont(buttonFont);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(displayCategoryButton, gbc);
        displayCategoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                expenseManager.displayCategoryExpenses();
            }
        });

        // 绘制时间折线图按钮（每日支出趋势）
        JButton timeLineChartButton = new JButton("Show Time-Line Chart");
        timeLineChartButton.setFont(buttonFont);
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(timeLineChartButton, gbc);
        timeLineChartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTimeLineChart();
            }
        });

        // 绘制种类饼图按钮
        JButton categoryPieChartButton = new JButton("Show Category Pie Chart");
        categoryPieChartButton.setFont(buttonFont);
        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(categoryPieChartButton, gbc);
        categoryPieChartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCategoryPieChart();
            }
        });

        // 显示分类和时间（年）按钮
        JButton displayCategoryAndYearButton = new JButton("Display Category and Year Expenses");
        displayCategoryAndYearButton.setFont(buttonFont);
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(displayCategoryAndYearButton, gbc);
        displayCategoryAndYearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                expenseManager.displayCategoryAndTimeExpenses();
            }
        });

        // 显示分类和时间（月）按钮
        JButton displayCategoryAndMonthButton = new JButton("Display Category and Month Expenses");
        displayCategoryAndMonthButton.setFont(buttonFont);
        gbc.gridx = 1; gbc.gridy = 4;
        panel.add(displayCategoryAndMonthButton, gbc);
        displayCategoryAndMonthButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                expenseManager.displayCategoryAndTimeExpenses();
            }
        });

        // 显示分类和时间（日）按钮
        JButton displayCategoryAndDayButton = new JButton("Display Category and Day Expenses");
        displayCategoryAndDayButton.setFont(buttonFont);
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(displayCategoryAndDayButton, gbc);
        displayCategoryAndDayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                expenseManager.displayCategoryAndTimeExpenses();
            }
        });

        // 设置框架可见
        frame.setVisible(true);
    }

    // 使用 JFreeChart 绘制时间折线图（按每日统计）
    private void showTimeLineChart() {
        Map<LocalDate, Double> dailyData = expenseManager.getDailySpendingData();
        TimeSeries series = new TimeSeries("Daily Spending");

        // 将数据添加到时间序列中
        for (Map.Entry<LocalDate, Double> entry : dailyData.entrySet()) {
            LocalDate date = entry.getKey();
            double amount = entry.getValue();
            series.add(new Day(date.getDayOfMonth(), date.getMonthValue(), date.getYear()), amount);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Daily Expense Line Chart", // 图表标题
                "Date",           // x轴标签
                "Expense",       // y轴标签
                dataset,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame chartFrame = new JFrame("Daily Expense Line Chart");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.add(chartPanel);
        chartFrame.pack();
        chartFrame.setVisible(true);
    }

    // 使用 JFreeChart 绘制消费种类饼图
    private void showCategoryPieChart() {
        Map<String, Double> categoryData = expenseManager.getCategorySpendingData();
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Double> entry : categoryData.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Spending by Category", // 图表标题
                dataset,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame chartFrame = new JFrame("Spending by Category Pie Chart");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.add(chartPanel);
        chartFrame.pack();
        chartFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ExpenseTrackerGUI();
            }
        });
    }
}
