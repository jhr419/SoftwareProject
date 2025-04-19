import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;
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

public class ExpenseTrackerGUI {
    private JFrame frame;
    private ExpenseManager expenseManager;

    public ExpenseTrackerGUI() {
        expenseManager = new ExpenseManager();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Kobe Xie - 财务管理");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLayout(new BorderLayout());

        // 设置背景色
        frame.getContentPane().setBackground(new Color(245, 245, 245));

        // 创建主面板，使用GridBagLayout布局
        JPanel panel = new JPanel(new GridBagLayout());
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // 设置图片（头像）
        int row = 0;
        String imagePath = "AI_assistant.jpg";  // 头像路径
        ImageIcon imageIcon = new ImageIcon(imagePath);
        Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(image));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(imageLabel, gbc);

        row++;
        // 添加“Transaction Management”分组标题
        addSectionTitle(panel, gbc, "交易管理", row);
        row++;
        // 添加按钮：添加交易，显示交易记录
        addButton(panel, gbc, "添加交易", row, 0, e -> addExpense());
        addButton(panel, gbc, "显示交易记录", row, 1, e -> expenseManager.displayExpenses());
        row++;
        // 添加更多按钮
        addButton(panel, gbc, "显示分类支出", row, 0, e -> expenseManager.displayCategoryExpenses());
        addButton(panel, gbc, "编辑数据", row, 1, e -> showExpenseTable());

        row++;
        // 添加“预算管理”分组标题
        addSectionTitle(panel, gbc, "预算管理", row);
        row++;
        // 设置预算按钮
        addButton(panel, gbc, "设置预算", row, 0, e -> setBudget());
        addButton(panel, gbc, "移除预算", row, 1, e -> removeBudget());
        row++;
        // 显示预算报告按钮
        addButton(panel, gbc, "显示预算报告", row, 0, e -> showBudgetReport());
        addButton(panel, gbc, "显示预算进度", row, 1, e -> showBudgetProgress());
        row++;
        // 显示按日期和类别的预算
        addButton(panel, gbc, "按日期显示预算", row, 0, e -> showBudgetsByDate());
        addButton(panel, gbc, "按类别显示预算", row, 1, e -> showBudgetsByCategory());

        row++;
        // 添加“数据可视化”分组标题
        addSectionTitle(panel, gbc, "数据可视化", row);
        row++;
        // 显示图表按钮
        addButton(panel, gbc, "显示时间线图", row, 0, e -> showTimeLineChart());
        addButton(panel, gbc, "显示分类饼图", row, 1, e -> showCategoryPieChart());

        row++;
        // 添加“储蓄管理”分组标题
        addSectionTitle(panel, gbc, "储蓄管理", row);
        row++;
        addButton(panel, gbc, "设置储蓄", row, 0, e -> setSavings());
        addButton(panel, gbc, "显示储蓄报告", row, 1, e -> showSavingsReport());

        row++;
        // 添加“报告”分组标题
        addSectionTitle(panel, gbc, "报告", row);
        row++;
        // 显示分类报告按钮
        addButton(panel, gbc, "显示分类报告", row, 0, e -> showClassificationReport());

        // 显示界面
        frame.setVisible(true);
    }

    private void addSectionTitle(JPanel panel, GridBagConstraints gbc, String title, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JLabel sectionLabel = new JLabel(title, SwingConstants.CENTER);
        sectionLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        sectionLabel.setForeground(new Color(0, 102, 204));  // 蓝色字体
        panel.add(sectionLabel, gbc);
    }

    private void addButton(JPanel panel, GridBagConstraints gbc, String text, int row, int col, ActionListener action) {
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setBackground(new Color(70, 130, 180));  // 深蓝色按钮
        button.setForeground(Color.WHITE);  // 白色文字
        button.setFocusPainted(false);  // 去掉按钮聚焦时的虚线框
        button.setContentAreaFilled(true);  // 去掉默认背景色
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 1, true));  // 添加边框
        button.addActionListener(action);
        panel.add(button, gbc);
    }

    // 修改按钮样式：添加圆角
    private void setButtonStyle(JButton button) {
        button.setBackground(new Color(70, 130, 180));  // 设置按钮背景色
        button.setForeground(Color.WHITE);  // 设置按钮文字颜色
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setFocusPainted(false);  // 去掉聚焦的虚线框
    }

    private void showBudgetsByCategory() {
        String category = JOptionPane.showInputDialog("Enter category:");
        if (category != null && !category.isEmpty()) {
            Map<LocalDate, Double> budgets = expenseManager.getBudgetsByCategory(category);

            if (budgets.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No budgets found for category: " + category, "No Data", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder report = new StringBuilder("Budgets for category: " + category + "\n");
                for (Map.Entry<LocalDate, Double> entry : budgets.entrySet()) {
                    report.append("End Date: ").append(entry.getKey()).append(" | Budget: ").append(entry.getValue()).append("\n");
                }
                JOptionPane.showMessageDialog(frame, report.toString(), "Category Budgets", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Category input cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showBudgetsByDate() {
        String dateStr = JOptionPane.showInputDialog("Enter date (YYYY-MM-DD):");
        try {
            LocalDate date = LocalDate.parse(dateStr);
            Map<String, Double> budgets = expenseManager.getBudgetsByDate(date);

            StringBuilder report = new StringBuilder("Budgets for " + date + ":\n");
            for (Map.Entry<String, Double> entry : budgets.entrySet()) {
                report.append("Category: " + entry.getKey() + " | Budget: " + entry.getValue() + "\n");
            }

            JOptionPane.showMessageDialog(frame, report.toString(), "Budgets for " + date, JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Invalid date format: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setSavings() {
        String category = JOptionPane.showInputDialog("Enter category:");
        String dateStr = JOptionPane.showInputDialog("Enter savings date (YYYY-MM-DD):");
        String amountStr = JOptionPane.showInputDialog("Enter savings amount:");

        try {
            LocalDate date = LocalDate.parse(dateStr);
            double amount = Double.parseDouble(amountStr);
            expenseManager.setSavings(category, date, amount);
            JOptionPane.showMessageDialog(frame, "Savings Set Successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 添加记录（交易）——提示用户输入交易类型（income 或 expense）
    private void addExpense() {
        String category = JOptionPane.showInputDialog("Enter category:");
        String amountStr = JOptionPane.showInputDialog("Enter amount:");
        String dateStr = JOptionPane.showInputDialog("Enter date (YYYY-MM-DD):");
        String itemName = JOptionPane.showInputDialog("Enter item name:");
        String transactionType = JOptionPane.showInputDialog("Enter transaction type (income/expense):");

        try {
            LocalDate date = LocalDate.parse(dateStr);
            double amount = Double.parseDouble(amountStr);
            expenseManager.addExpense(category, amount, date, itemName, transactionType);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 预算管理：设置预算
    private void setBudget() {
        String category = JOptionPane.showInputDialog("Enter category:");
        String startDateStr = JOptionPane.showInputDialog("Enter budget start date (YYYY-MM-DD):");
        String endDateStr = JOptionPane.showInputDialog("Enter budget end date (YYYY-MM-DD):");
        String amountStr = JOptionPane.showInputDialog("Enter budget amount:");

        try {
            LocalDate endDate = LocalDate.parse(endDateStr);
            double amount = Double.parseDouble(amountStr);
            expenseManager.setBudget(category, endDate, amount);
            JOptionPane.showMessageDialog(frame, "Budget Set Successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeBudget() {
        Map<String, Map<LocalDate, Double>> budgets = expenseManager.getAllBudgets();

        if (budgets.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No budgets available to remove.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] budgetOptions = budgets.entrySet().stream()
                .flatMap(categoryEntry -> categoryEntry.getValue().entrySet().stream()
                        .map(dateEntry -> categoryEntry.getKey() + " (" + dateEntry.getKey() + ")"))
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(
                frame,
                "Select Budget to Remove:",
                "Remove Budget",
                JOptionPane.QUESTION_MESSAGE,
                null,
                budgetOptions,
                budgetOptions[0]
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
    private void showSavingsReport() {
        String category = JOptionPane.showInputDialog("Enter category (leave empty for all):");
        Map<String, Map<LocalDate, Double>> savings = expenseManager.getAllSavings();
        StringBuilder report = new StringBuilder("Savings Report:\n");

        for (String cat : savings.keySet()) {
            if (category.isEmpty() || category.equalsIgnoreCase(cat)) {
                for (LocalDate date : savings.get(cat).keySet()) {
                    report.append(cat).append(" (").append(date).append("): ").append(savings.get(cat).get(date)).append("\n");
                }
            }
        }
        JOptionPane.showMessageDialog(frame, report.toString(), "Savings Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showBudgetProgress() {
        String category = JOptionPane.showInputDialog("Enter category:");
        String startDateStr = JOptionPane.showInputDialog("Enter budget start date (YYYY-MM-DD):");
        String endDateStr = JOptionPane.showInputDialog("Enter budget end date (YYYY-MM-DD):");

        try {
            LocalDate budgetSetDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            double progress = expenseManager.getBudgetProgress(category, budgetSetDate, endDate) * 100;

            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setValue((int) progress);
            progressBar.setStringPainted(true);

            JOptionPane.showMessageDialog(frame, progressBar, "Budget Progress", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 显示收入和支出折线图：收入和出账各为一条曲线
    private void showTimeLineChart() {
        Map<LocalDate, Double> expenseData = expenseManager.getDailySpendingData();
        Map<LocalDate, Double> incomeData = expenseManager.getDailyIncomeData();

        TimeSeries expenseSeries = new TimeSeries("Expense");
        for (Map.Entry<LocalDate, Double> entry : expenseData.entrySet()) {
            LocalDate date = entry.getKey();
            expenseSeries.add(new Day(date.getDayOfMonth(), date.getMonthValue(), date.getYear()), entry.getValue());
        }

        TimeSeries incomeSeries = new TimeSeries("Income");
        for (Map.Entry<LocalDate, Double> entry : incomeData.entrySet()) {
            LocalDate date = entry.getKey();
            incomeSeries.add(new Day(date.getDayOfMonth(), date.getMonthValue(), date.getYear()), entry.getValue());
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(expenseSeries);
        dataset.addSeries(incomeSeries);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Daily Transaction Line Chart",
                "Date",
                "Amount",
                dataset,
                true,
                true,
                false
        );

        DateAxis xAxis = (DateAxis) chart.getXYPlot().getDomainAxis();
        xAxis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));
        xAxis.setVerticalTickLabels(true);

        showChart(chart, "Daily Transaction Line Chart");
    }

    // 数据可视化 - 饼图（仅显示出账数据）
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

    // 显示 CSV 数据窗口，使用 JTable 展示，并支持编辑/删除记录
    private void showExpenseTable() {
        JFrame tableFrame = new JFrame("CSV Transaction Records");
        tableFrame.setSize(700, 400);
        tableFrame.setLayout(new BorderLayout());

        String[] columns = {"Category", "Amount", "Date", "Item Name", "Type"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        List<ExpenseRecord> expenseList = expenseManager.getExpenses();
        for (ExpenseRecord record : expenseList) {
            Object[] row = {
                    record.getCategory(),
                    record.getAmount(),
                    record.getDate(),
                    record.getItemName(),
                    record.getTransactionType()
            };
            model.addRow(row);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        tableFrame.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("Delete Selected");

        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String currentCategory = (String) model.getValueAt(selectedRow, 0);
                String currentAmount = model.getValueAt(selectedRow, 1).toString();
                String currentDate = model.getValueAt(selectedRow, 2).toString();
                String currentItemName = (String) model.getValueAt(selectedRow, 3);
                String currentType = (String) model.getValueAt(selectedRow, 4);

                String newCategory = JOptionPane.showInputDialog(tableFrame, "Enter new category:", currentCategory);
                String newAmountStr = JOptionPane.showInputDialog(tableFrame, "Enter new amount:", currentAmount);
                String newDate = JOptionPane.showInputDialog(tableFrame, "Enter new date (YYYY-MM-DD):", currentDate);
                String newItemName = JOptionPane.showInputDialog(tableFrame, "Enter new item name:", currentItemName);
                String newType = JOptionPane.showInputDialog(tableFrame, "Enter new transaction type (income/expense):", currentType);

                try {
                    double newAmount = Double.parseDouble(newAmountStr);
                    LocalDate parsedDate = LocalDate.parse(newDate);
                    ExpenseRecord newRecord = new ExpenseRecord(newCategory, newAmount, parsedDate, newItemName, newType);
                    boolean success = expenseManager.updateExpense(selectedRow, newRecord);
                    if (success) {
                        model.setValueAt(newCategory, selectedRow, 0);
                        model.setValueAt(newAmount, selectedRow, 1);
                        model.setValueAt(parsedDate, selectedRow, 2);
                        model.setValueAt(newItemName, selectedRow, 3);
                        model.setValueAt(newType, selectedRow, 4);
                        JOptionPane.showMessageDialog(tableFrame, "Record updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(tableFrame, "Failed to update record!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(tableFrame, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(tableFrame, "Please select a record to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(tableFrame, "Are you sure you want to delete this record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = expenseManager.deleteExpense(selectedRow);
                    if (success) {
                        model.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(tableFrame, "Record deleted successfully!");
                    } else {
                        JOptionPane.showMessageDialog(tableFrame, "Failed to delete record!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(tableFrame, "Please select a record to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        tableFrame.add(buttonPanel, BorderLayout.SOUTH);

        tableFrame.setVisible(true);
    }

    // 显示收入和支出分类统计报表
    private void showClassificationReport() {
        Map<String, Map<String, Double>> classification = expenseManager.getClassificationData();
        StringBuilder report = new StringBuilder("Classification Report:\n\n");
        report.append("Income:\n");
        for (Map.Entry<String, Double> entry : classification.get("income").entrySet()) {
            report.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        report.append("\nExpense:\n");
        for (Map.Entry<String, Double> entry : classification.get("expense").entrySet()) {
            report.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        JOptionPane.showMessageDialog(frame, report.toString(), "Classification Report", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenseTrackerGUI::new);
    }
}
