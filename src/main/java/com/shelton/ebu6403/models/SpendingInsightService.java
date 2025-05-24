package com.shelton.ebu6403.models;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates intelligent financial advice based on recent expense records.
 * <p>
 * This service analyzes expenses, detects seasonal spending patterns,
 * integrates holiday context, and sends prompts to an AI API for budgeting insights.
 * </p>
 *
 * author Zhifei Liu, Weicheng Xie, Haihan Sun
 */
public class SpendingInsightService {

    private List<ExpenseRecord> expenses;
    private ApiClient apiClient;

    /**
     * Constructs the insight service with given data and API client.
     *
     * @param expenses   list of historical transaction records
     * @param apiClient  instance of ApiClient to access external AI API
     */
    public SpendingInsightService(List<ExpenseRecord> expenses, ApiClient apiClient) {
        this.expenses = expenses;
        this.apiClient = apiClient;
    }

    /**
     * Generates spending insights using AI.
     * <p>
     * This method constructs a prompt based on recent expense records,
     * adds seasonal reminders and suggestions, and submits the prompt
     * to the AI model for analysis.
     * </p>
     *
     * @return financial advice in plain English from the AI
     */
    public String generateSpendingInsights() {
        try {
            StringBuilder prompt = new StringBuilder();

            // Add reminder if a holiday is near
            ChineseHolidayAnalyzer holidayAnalyzer = new ChineseHolidayAnalyzer(expenses);
            String upcomingReminder = holidayAnalyzer.getUpcomingHolidayReminder();
            if (!upcomingReminder.isBlank()) {
                prompt.append("Important Reminder:\n").append(upcomingReminder).append("\n\n");
            }

            prompt.append("Below are recent user expense records. Please generate the following in plain English (no markdown, no symbols):\n")
                    .append("1. Spending Analysis\n")
                    .append("2. Monthly Budget Advice (With consideration of holidays in the next 30 days)\n")
                    .append("3. Saving Advice\n")
                    .append("4. Expense Reduction Advice\n\n");

            // Add seasonal context
            String seasonalContext = holidayAnalyzer.generateSeasonalContext();
            if (seasonalContext != null && !seasonalContext.isBlank()) {
                prompt.append("Here is some seasonal spending context:\n").append(seasonalContext).append("\n\n");
            }

            // Add holiday-specific budgeting advice
            String holidayBudgetAdvice = getHolidayBudgetAdvice();
            if (!holidayBudgetAdvice.isBlank()) {
                prompt.append("Here is a seasonal budgeting tip:\n").append(holidayBudgetAdvice).append("\n\n");
            }

            // Collect records from the past 30 days
            LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
            List<ExpenseRecord> recent = expenses.stream()
                    .filter(record -> record.getTransactionType().equalsIgnoreCase("expense"))
                    .filter(record -> record.getDate() != null && !record.getDate().isBefore(oneMonthAgo))
                    .collect(Collectors.toList());

            for (ExpenseRecord record : recent) {
                prompt.append("Date: ").append(record.getDate())
                        .append(", Item: ").append(record.getItemName())
                        .append(", Category: ").append(record.getCategory())
                        .append(", Amount: ").append(record.getAmount()).append(" RMB\n");
            }

            return apiClient.sendRequest(prompt.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to retrieve financial advice. Please check your network or API configuration.";
        }
    }

    /**
     * Returns budgeting suggestions based on upcoming Chinese holidays.
     * <p>
     * If a major holiday is within the next 7 days, returns a short tip for budgeting.
     * </p>
     *
     * @return holiday-related budgeting tip or empty string if no upcoming holidays
     */
    private String getHolidayBudgetAdvice() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();

        if (!today.isBefore(LocalDate.of(year, 1, 13)) && today.isBefore(LocalDate.of(year, 1, 20))) {
            return "Spring Festival is approaching. Consider increasing your Food or Gift budget.";
        } else if (!today.isBefore(LocalDate.of(year, 3, 28)) && today.isBefore(LocalDate.of(year, 4, 4))) {
            return "Qingming Festival is approaching. You may prepare for travel, flowers, or memorial spending.";
        } else if (!today.isBefore(LocalDate.of(year, 4, 24)) && today.isBefore(LocalDate.of(year, 5, 1))) {
            return "May Day Holiday is approaching. Consider planning your Travel or Entertainment budget.";
        } else if (!today.isBefore(LocalDate.of(year, 5, 23)) && today.isBefore(LocalDate.of(year, 6, 1))) {
            return "Children's Day is approaching. Consider budgeting for gifts, toys or family outings.";
        } else if (!today.isBefore(LocalDate.of(year, 5, 30)) && today.isBefore(LocalDate.of(year, 6, 7))) {
            return "Dragon Boat Festival is approaching. Consider food and activity expenses.";
        } else if (!today.isBefore(LocalDate.of(year, 9, 10)) && today.isBefore(LocalDate.of(year, 9, 17))) {
            return "Mid-Autumn Festival is coming. Prepare for family dinners or gift purchases.";
        } else if (!today.isBefore(LocalDate.of(year, 9, 24)) && today.isBefore(LocalDate.of(year, 10, 1))) {
            return "National Day is coming. Plan ahead for Home or Travel spending.";
        }

        return "";
    }

}
