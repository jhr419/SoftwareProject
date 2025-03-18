import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.Map;

// JFreeChart 相关导入
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Day;
import org.jfree.chart.axis.DateAxis;
import java.text.SimpleDateFormat;

//测试一下git
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
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());

        // **创建主面板**
        JPanel panel = new JPanel(new GridBagLayout());
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // **加载并显示图片**
        String imagePath = "AI_assistant.jpg";
        ImageIcon imageIcon = new ImageIcon(imagePath);
        Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(image));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(imageLabel, gbc);

        // **支出管理**
        addSectionTitle(panel, gbc, "Expense Management", 1);
        addButton(panel, gbc, "Add Expense", 2, e -> addExpense());
        addButton(panel, gbc, "Display Expenses", 3, e -> expenseManager.displayExpenses());
        addButton(panel, gbc, "Display Category Expenses", 4, e -> expenseManager.displayCategoryExpenses());

        // **预算管理**
        addSectionTitle(panel, gbc, "Budget Management", 5);
        addButton(panel, gbc, "Set Budget (Category + Date)", 6, e -> setBudget());
        addButton(panel, gbc, "Remove Budget", 7, e -> removeBudget());
        addButton(panel, gbc, "Show Budget Report", 8, e -> showBudgetReport());
        addButton(panel, gbc, "Show Budget Progress", 9, e -> showBudgetProgress());

        // **数据可视化**
        addSectionTitle(panel, gbc, "Data Visualization", 10);
        addButton(panel, gbc, "Show Time-Line Chart", 11, e -> showTimeLineChart());
        addButton(panel, gbc, "Show Category Pie Chart", 12, e -> showCategoryPieChart());

        frame.setVisible(true);
    }

    // **添加分组标题**
    private void addSectionTitle(JPanel panel, GridBagConstraints gbc, String title, int row) {
        JLabel sectionLabel = new JLabel(title, SwingConstants.CENTER);
        sectionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(sectionLabel, gbc);
    }

    // **添加按钮**
    private void addButton(JPanel panel, GridBagConstraints gbc, String text, int row, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        button.addActionListener(action);
        panel.add(button, gbc);
    }

    // **支出管理**
    private void addExpense() {
        String category = JOptionPane.showInputDialog("Enter category:");
        String amountStr = JOptionPane.showInputDialog("Enter amount:");
        String dateStr = JOptionPane.showInputDialog("Enter date (YYYY-MM-DD):");
        String itemName = JOptionPane.showInputDialog("Enter item name:");
        LocalDate date = LocalDate.parse(dateStr);
        double amount = Double.parseDouble(amountStr);
        expenseManager.addExpense(category, amount, date, itemName);
    }

    // **预算管理**
    // **设置预算**
    private void setBudget() {
        String category = JOptionPane.showInputDialog("Enter category:");
        String startDateStr = JOptionPane.showInputDialog("Enter budget start date (YYYY-MM-DD):");
        String endDateStr = JOptionPane.showInputDialog("Enter budget end date (YYYY-MM-DD):");
        String amountStr = JOptionPane.showInputDialog("Enter budget amount:");

        LocalDate budgetSetDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);
        double amount = Double.parseDouble(amountStr);

        expenseManager.setBudget(category, budgetSetDate, endDate, amount);
        JOptionPane.showMessageDialog(frame, "Budget Set Successfully!");
    }

    private void removeBudget() {
        Map<String, Map<LocalDate, Double>> budgets = expenseManager.getAllBudgets();

        if (budgets.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No budgets available to remove.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // **创建预算列表**
        String[] budgetOptions = budgets.entrySet().stream()
                .flatMap(categoryEntry -> categoryEntry.getValue().entrySet().stream()
                        .map(dateEntry -> categoryEntry.getKey() + " (" + dateEntry.getKey() + ")"))
                .toArray(String[]::new);

        // **让用户从下拉菜单中选择要删除的预算**
        String selected = (String) JOptionPane.showInputDialog(
                frame,
                "Select Budget to Remove:",
                "Remove Budget",
                JOptionPane.QUESTION_MESSAGE,
                null,
                budgetOptions,
                budgetOptions[0] // 默认选中第一个
        );

        if (selected != null) {
            String[] parts = selected.split(" \\(");
            String category = parts[0];
            LocalDate endDate = LocalDate.parse(parts[1].replace(")", ""));
            boolean success = expenseManager.removeBudget(category, endDate);

            if (success) {
                JOptionPane.showMessageDialog(frame, "Budget Removed Successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to remove budget!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void showBudgetReport() {
        String category = JOptionPane.showInputDialog("Enter category (leave empty for all):");
        Map<String, Map<LocalDate, Double>> budgets = expenseManager.getAllBudgets();
        StringBuilder report = new StringBuilder("Budget Report:\n");

        for (String cat : budgets.keySet()) {
            if (category.isEmpty() || category.equalsIgnoreCase(cat)) {
                for (LocalDate date : budgets.get(cat).keySet()) {
                    report.append(cat).append(" (").append(date).append("): ").append(budgets.get(cat).get(date)).append("\n");
                }
            }
        }
        JOptionPane.showMessageDialog(frame, report.toString(), "Budget Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showBudgetProgress() {
        String category = JOptionPane.showInputDialog("Enter category:");
        String startDateStr = JOptionPane.showInputDialog("Enter budget start date (YYYY-MM-DD):");
        String endDateStr = JOptionPane.showInputDialog("Enter budget end date (YYYY-MM-DD):");

        LocalDate budgetSetDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        double progress = expenseManager.getBudgetProgress(category, budgetSetDate, endDate) * 100;

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) progress);
        progressBar.setStringPainted(true);

        JOptionPane.showMessageDialog(frame, progressBar, "Budget Progress", JOptionPane.INFORMATION_MESSAGE);
    }



    // **数据可视化 - 时间折线图**
    private void showTimeLineChart() {
        Map<LocalDate, Double> dailyData = expenseManager.getDailySpendingData();
        TimeSeries series = new TimeSeries("Daily Spending");

        for (Map.Entry<LocalDate, Double> entry : dailyData.entrySet()) {
            LocalDate date = entry.getKey();
            double amount = entry.getValue();
            series.add(new Day(date.getDayOfMonth(), date.getMonthValue(), date.getYear()), amount);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);

        // **创建图表**
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Daily Expense Line Chart",
                "Date",
                "Expense",
                dataset,
                true,
                true,
                false
        );

        // **强制设置 X 轴格式（按具体日期显示）**
        DateAxis xAxis = (DateAxis) chart.getXYPlot().getDomainAxis();
        xAxis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd")); // **设置日期格式**
        xAxis.setVerticalTickLabels(true); // **垂直显示日期，防止重叠**

        showChart(chart, "Daily Expense Line Chart");
    }
    // **数据可视化 - 饼图**
    private void showCategoryPieChart() {
        Map<String, Double> categoryData = expenseManager.getCategorySpendingData();
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Double> entry : categoryData.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart("Spending by Category", dataset, true, true, false);
        showChart(chart, "Spending by Category Pie Chart");
    }

    private void showChart(JFreeChart chart, String title) {
        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame chartFrame = new JFrame(title);
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.add(chartPanel);
        chartFrame.pack();
        chartFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenseTrackerGUI::new);
    }
}
