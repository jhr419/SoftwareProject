import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.border.*;
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
import java.util.stream.Collectors;

public class ExpenseTrackerGUI {
    private JFrame frame;
    private ExpenseManager expenseManager;
    private List<FixedIncome> fixedIncomes = new ArrayList<>();
    private FixedIncomeDataHandler dataHandler = new FixedIncomeDataHandler();

    public ExpenseTrackerGUI() {
        expenseManager = new ExpenseManager("sk-cbzpgeqjquxjgusngdklsmrzikmptukukbrvzbjhibsosfyf");
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Finance Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLayout(new BorderLayout());

        // 加载固定收入数据
        fixedIncomes = dataHandler.loadFixedIncomes();

        // 设置背景色
        frame.getContentPane().setBackground(new Color(245, 245, 245));
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245)); // 使面板背景与窗口一致
        // 创建 JScrollPane 将按钮面板包裹起来
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;


        // 设置图片（头像）
        int row = 0;
        //String imagePath = "AI_assistant.jpg";  // 头像路径
        //ImageIcon imageIcon = new ImageIcon(imagePath);
        //Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        //JLabel imageLabel = new JLabel(new ImageIcon(image));
        //gbc.gridx = 0;
        //gbc.gridy = row;
        //gbc.gridwidth = 2;
        //panel.add(imageLabel, gbc);

        addSectionTitle(panel, gbc, "Transaction Management", row);
        row++;
        // 第一行按钮：Add Transaction | Display Transactions
        addButton(panel, gbc, "Add Transaction", row, 0, e -> addExpense());
        addButton(panel, gbc, "Display Transactions", row, 1, e -> showAllTransactions());
        row++;
        // 第二行按钮：Display Category Expenses | Load CSV Data
        addButton(panel, gbc, "Display Category Expenses", row, 0, e -> showCategoryExpenses());
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
        // Savings 分组标题
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
        reportButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        reportButton.setBackground(new Color(33, 150, 243)); // 按钮颜色
        reportButton.setForeground(Color.WHITE);  // 字体颜色
        reportButton.setBorder(new RoundedBorder(20));  // 更大的圆角
        reportButton.setFocusPainted(false);
        reportButton.addActionListener(e -> showClassificationReport());
        panel.add(reportButton, gbc);

        row++;
        //AI Asistance
        addSectionTitle(panel, gbc, "AI Assistance", row);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JButton AIqaButton = new JButton("AI_QA Help");
        AIqaButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        AIqaButton.setBackground(new Color(33, 150, 243)); // 按钮颜色
        AIqaButton.setForeground(Color.WHITE);  // 字体颜色
        AIqaButton.setBorder(new RoundedBorder(20));  // 更大的圆角
        AIqaButton.setFocusPainted(false);
        AIqaButton.addActionListener(e -> openChatUI());
        panel.add(AIqaButton, gbc);

        row++;
        addSectionTitle(panel, gbc, "Fixed Income Management", row);
        row++;
        addButton(panel, gbc, "Add Fixed Income", row, 0, e -> addFixedIncome());
        addButton(panel, gbc, "View Fixed Incomes", row, 1, e -> viewFixedIncomes());
        row++;
        addButton(panel, gbc, "Delete Fixed Income", row, 0, e -> deleteFixedIncome());

        frame.setVisible(true);
    }

    private JButton createModernButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setBackground(new Color(33, 150, 243));  // 按钮背景颜色
        button.setForeground(Color.WHITE);  // 字体颜色
        button.setBorder(new RoundedBorder(20));  // 圆角按钮
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 50));  // 增加按钮的高度，使其更大，易于点击
        button.addActionListener(action);
        return button;
    }

    private void addSectionTitle(JPanel panel, GridBagConstraints gbc, String title, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JLabel sectionLabel = new JLabel(title, SwingConstants.CENTER);
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));  // 更现代的字体
        sectionLabel.setForeground(new Color(33, 150, 243));  // 标题颜色
        sectionLabel.setOpaque(true);
        sectionLabel.setBackground(new Color(230, 230, 230));  // 背景颜色
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // 边距
        panel.add(sectionLabel, gbc);
    }

    private void addButton(JPanel panel, GridBagConstraints gbc, String text, int row, int col, ActionListener action) {
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JButton button = createModernButton(text, action);
        panel.add(button, gbc);
    }


    private void addSection(JPanel panel, String sectionTitle, String[] buttonLabels, ActionListener[] actions) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 分区标题
        JLabel sectionLabel = new JLabel(sectionTitle);
        sectionLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        sectionLabel.setForeground(new Color(33, 150, 243));
        sectionPanel.add(sectionLabel);

        // 按钮
        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = createButton(buttonLabels[i], actions[i]);
            sectionPanel.add(button);
            sectionPanel.add(Box.createVerticalStrut(10));  // 按钮之间的间隔
        }

        panel.add(sectionPanel);
        panel.add(Box.createVerticalStrut(20));  // 分区之间的间隔
    }

    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        button.setBackground(new Color(33, 150, 243));
        button.setForeground(Color.WHITE);
        button.setBorder(new RoundedBorder(10));  // 圆角边框
        button.setFocusPainted(false);
        button.addActionListener(action);
        button.setPreferredSize(new Dimension(200, 40));
        return button;
    }
    // 设置圆角边框类
    static class RoundedBorder implements Border {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(5, 5, 5, 5);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(c.getForeground());
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    private void showCategoryExpenses() {
        StringBuilder categoryExpenses = new StringBuilder("Classified Expense:\n");

        // 获取分类支出统计数据
        Map<String, Double> categoryTotals = new HashMap<>();
        for (ExpenseRecord record : expenseManager.getExpenses()) {
            if (record.getTransactionType().equalsIgnoreCase("expense")) {
                String category = record.getAiCategory();
                double amount = record.getAmount();
                categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
            }
        }

        // 拼接分类支出数据
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            categoryExpenses.append("Category: ").append(entry.getKey()).append(" | Total Expense: ").append(entry.getValue()).append("\n");
        }

        // 使用JOptionPane弹出对话框显示分类支出
        JOptionPane.showMessageDialog(frame, categoryExpenses.toString(), "Classified Expense", JOptionPane.INFORMATION_MESSAGE);
    }

    // 修改按钮样式：添加圆角
    private void setButtonStyle(JButton button) {
        button.setBackground(new Color(70, 130, 180));  // 设置按钮背景色
        button.setForeground(Color.WHITE);  // 设置按钮文字颜色
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setFocusPainted(false);  // 去掉聚焦的虚线框
    }

    private void showAllTransactions() {
        StringBuilder transactions = new StringBuilder("ALL Transactions:\n");

        // 获取所有交易记录
        for (ExpenseRecord record : expenseManager.getExpenses()) {
            transactions.append(record.toString()).append("\n");
        }

        // 创建文本区域，用来显示交易记录
        JTextArea textArea = new JTextArea(transactions.toString());
        textArea.setEditable(false);  // 设置文本区域为不可编辑
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        textArea.setLineWrap(true);  // 自动换行
        textArea.setWrapStyleWord(true);  // 单词折行

        // 将文本区域添加到滚动面板中
        JScrollPane scrollPane = new JScrollPane(textArea);

        // 设置滚动面板的大小，使其适应你需要的显示区域
        scrollPane.setPreferredSize(new Dimension(600, 400));  // 设置滚动面板的宽度和高度

        // 弹出窗口显示滚动的交易记录
        JOptionPane.showMessageDialog(frame, scrollPane, "ALL Transactions:", JOptionPane.INFORMATION_MESSAGE);
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
        String dateStr = JOptionPane.showInputDialog("Enter End Date (YYYY-MM-DD):");
        try {
            LocalDate date = LocalDate.parse(dateStr);
            Map<String, Double> budgets = expenseManager.getBudgetsByDate(date);

            StringBuilder report = new StringBuilder("Budget closest to entered date " + date + ":\n");
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
        String itemName = JOptionPane.showInputDialog("Enter item name:");
        String amountStr = JOptionPane.showInputDialog("Enter amount:");
        String dateStr = JOptionPane.showInputDialog("Enter date (YYYY-MM-DD):");
        String transactionType = JOptionPane.showInputDialog("Enter transaction type (income/expense):");

        try {
            LocalDate date = LocalDate.parse(dateStr);
            double amount = Double.parseDouble(amountStr);
            expenseManager.addExpense(itemName, amount, date, transactionType);
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

            // 获取符合条件的所有预算
            Map<LocalDate, Double> relevantBudgets = expenseManager.getBudgetsByCategory(category).entrySet().stream()
                    .filter(entry -> !entry.getKey().isBefore(budgetSetDate) && !entry.getKey().isAfter(endDate))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // 如果没有找到相关的预算，提示用户
            if (relevantBudgets.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No budgets found for the specified category and date range.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 创建一个面板来显示多个进度条
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  // 垂直布局显示多个进度条

            // 显示每个符合条件的预算的进度条
            for (Map.Entry<LocalDate, Double> budgetEntry : relevantBudgets.entrySet()) {
                LocalDate budgetEndDate = budgetEntry.getKey();
                double budgetAmount = budgetEntry.getValue();

                // 获取进度：调用 getBudgetProgress 方法计算进度
                double progress = expenseManager.getBudgetProgress(category, budgetSetDate, budgetEndDate) * 100;

                // 创建进度条
                JProgressBar progressBar = new JProgressBar(0, 100);
                progressBar.setValue((int) (progress));
                progressBar.setStringPainted(true);
                progressBar.setString(String.format("Progress for %s (End Date: %s): %.2f%%", category, budgetEndDate, progress));

                // 将进度条添加到面板
                panel.add(progressBar);
                panel.add(Box.createVerticalStrut(10));  // 添加间隔
            }

            // 弹出窗口显示包含进度条的面板
            JOptionPane.showMessageDialog(frame, panel, "Budget Progress", JOptionPane.INFORMATION_MESSAGE);

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
        Map<String, Double> categoryData = expenseManager.getAiCategorySpendingData();
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
                    record.getAiCategory(),
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

                    // 根据新输入的 itemName 获取 AI 分类
                    //String newAiCategory = expenseManager.classifyWithAI(newItemName);  // 获取新的 AI 分类

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



    // 添加固定收入
    private void addFixedIncome() {
        String source = JOptionPane.showInputDialog("输入收入来源:");
        String amountStr = JOptionPane.showInputDialog("输入收入金额:");
        String period = JOptionPane.showInputDialog("输入收入周期 (如每月/每季度):");

        try {
            double amount = Double.parseDouble(amountStr);
            FixedIncome income = new FixedIncome(source, amount, period);
            fixedIncomes.add(income);
            dataHandler.saveFixedIncomes(fixedIncomes); // 保存到文件
            JOptionPane.showMessageDialog(frame, "固定收入添加成功!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "无效的金额输入!", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 查看固定收入
    private void viewFixedIncomes() {
        if (fixedIncomes.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "当前没有固定收入记录!", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder message = new StringBuilder("固定收入列表:\n");
        for (int i = 0; i < fixedIncomes.size(); i++) {
            message.append(i + 1).append(". ").append(fixedIncomes.get(i)).append("\n");
        }

        JOptionPane.showMessageDialog(frame, message.toString(), "固定收入", JOptionPane.INFORMATION_MESSAGE);
    }

    // 删除固定收入
    private void deleteFixedIncome() {
        if (fixedIncomes.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "当前没有固定收入记录!", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder message = new StringBuilder("固定收入列表:\n");
        for (int i = 0; i < fixedIncomes.size(); i++) {
            message.append(i + 1).append(". ").append(fixedIncomes.get(i)).append("\n");
        }
        message.append("\n请输入要删除的固定收入编号:");

        String input = JOptionPane.showInputDialog(frame, message.toString(), "删除固定收入", JOptionPane.QUESTION_MESSAGE);
        if (input != null) {
            try {
                int index = Integer.parseInt(input) - 1;
                if (index >= 0 && index < fixedIncomes.size()) {
                    fixedIncomes.remove(index);
                    dataHandler.saveFixedIncomes(fixedIncomes); // 保存到文件
                    JOptionPane.showMessageDialog(frame, "固定收入删除成功!");
                } else {
                    JOptionPane.showMessageDialog(frame, "无效的编号!", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "无效的输入!", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenseTrackerGUI::new);
    }
}
