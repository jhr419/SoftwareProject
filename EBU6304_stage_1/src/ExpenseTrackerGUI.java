import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
        frame = new JFrame("Kobe Xie");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        int row = 0;
        // 图片（独占一行，两列）
        String imagePath = "AI_assistant.jpg";
        ImageIcon imageIcon = new ImageIcon(imagePath);
        Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(image));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(imageLabel, gbc);

        row++;
        // Transaction Management 分组标题
        addSectionTitle(panel, gbc, "Transaction Management", row);
        addButton(panel, gbc, "AI Assistant", row, 30, e -> openChatUI()); // 点击时打开 ChatUI
        row++;
        // 第一行按钮：Add Transaction | Display Transactions
        addButton(panel, gbc, "Add Transaction", row, 0, e -> addExpense());
        addButton(panel, gbc, "Display Transactions", row, 1, e -> expenseManager.displayExpenses());
        row++;
        // 第二行按钮：Display Category Expenses | Load CSV Data
        addButton(panel, gbc, "Display Category Expenses", row, 0, e -> expenseManager.displayCategoryExpenses());
        addButton(panel, gbc, "Edit Data", row, 1, e -> showExpenseTable());

        row++;
        // Budget Management 分组标题
        addSectionTitle(panel, gbc, "Budget Management", row);
        row++;
        // 第一行按钮：Set Budget (Category + Date) | Remove Budget
        addButton(panel, gbc, "Set Budget (Category + Date)", row, 0, e -> setBudget());
        addButton(panel, gbc, "Remove Budget", row, 1, e -> removeBudget());
        row++;
        // 第二行按钮：Show Budget Report | Show Budget Progress
        addButton(panel, gbc, "Show Budget Report", row, 0, e -> showBudgetReport());
        addButton(panel, gbc, "Show Budget Progress", row, 1, e -> showBudgetProgress());
        row++;
        addButton(panel, gbc, "Show Budgets by Date", row, 0, e -> showBudgetsByDate());
        // 添加 "Show Budgets by Category" 按钮
        addButton(panel, gbc, "Show Budgets by Category", row, 1, e -> showBudgetsByCategory());

        row++;
        // Data Visualization 分组标题
        addSectionTitle(panel, gbc, "Data Visualization", row);
        row++;
        // 一行按钮：Show Time-Line Chart | Show Category Pie Chart
        addButton(panel, gbc, "Show Time-Line Chart", row, 0, e -> showTimeLineChart());
        addButton(panel, gbc, "Show Category Pie Chart", row, 1, e -> showCategoryPieChart());

        row++;
        addSectionTitle(panel, gbc, "Savings", row);
        row++;
        addButton(panel, gbc, "Set Savings", row, 0, e -> setSavings());
        addButton(panel, gbc, "Show Savings Report", row, 1, e -> showSavingsReport());

        row++;
        // Reports 分组标题
        addSectionTitle(panel, gbc, "Reports", row);


        row++;
        // 单个按钮居中：Show Classification Report
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JButton reportButton = new JButton("Show Classification Report");
        reportButton.setFont(new Font("Arial", Font.PLAIN, 14));
        reportButton.addActionListener(e -> showClassificationReport());
        panel.add(reportButton, gbc);

        frame.setVisible(true);
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

    // 辅助方法：添加分组标题（横跨两列）
    private void addSectionTitle(JPanel panel, GridBagConstraints gbc, String title, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JLabel sectionLabel = new JLabel(title, SwingConstants.CENTER);
        sectionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(sectionLabel, gbc);
    }

    // 辅助方法：在指定行列添加按钮，gridwidth 固定为1
    private void addButton(JPanel panel, GridBagConstraints gbc, String text, int row, int col, ActionListener action) {
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.addActionListener(action);
        panel.add(button, gbc);
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
    // 方法：打开 ChatUI
    private void openChatUI() {
        // 创建并显示 ChatUI 窗口
        ChatUI chatUI = new ChatUI();
        chatUI.getFrame().setVisible(true); // 显示 ChatUI 窗口
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenseTrackerGUI::new);
    }
}
