package main.java.com.shelton.ebu6403.models;

import java.util.List;

public class SpendingInsightService {

    private List<ExpenseRecord> expenses;
    private ApiClient apiClient;

    public SpendingInsightService(List<ExpenseRecord> expenses, ApiClient apiClient) {
        this.expenses = expenses;
        this.apiClient = apiClient;
    }

    public String generateSpendingInsights() {
        try {
            StringBuilder prompt = new StringBuilder();
            prompt.append("Below are recent user expense records. Please generate the following in plain English (no markdown, no symbols):\n")
                    .append("1. Spending Analysis\n")
                    .append("2. Monthly Budget Advice\n")
                    .append("3. Saving Advice\n")
                    .append("4. Expense Reduction Advice\n\n");

            // 添加：节日消费背景分析
            ChineseHolidayAnalyzer holidayAnalyzer = new ChineseHolidayAnalyzer(expenses);
            String seasonalContext = holidayAnalyzer.generateSeasonalContext();
            if (seasonalContext != null && !seasonalContext.isBlank()) {
                prompt.append("Here is some seasonal spending context:\n").append(seasonalContext).append("\n\n");
            }

            int startIdx = Math.max(0, expenses.size() - 30);
            List<ExpenseRecord> recent = expenses.subList(startIdx, expenses.size());

            for (ExpenseRecord record : recent) {
                if (!record.getTransactionType().equalsIgnoreCase("expense")) continue;
                prompt.append("Date: ").append(record.getDate())
                        .append(", Item: ").append(record.getItemName())
                        .append(", Category: ").append(record.getCategory())
                        .append(", Amount: ").append(record.getAmount()).append(" RMB\n");
            }
            //System.out.println("Final Prompt to AI:\n" + prompt);
            return apiClient.sendRequest(prompt.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to retrieve financial advice. Please check your network or API configuration.";
        }
    }

}
