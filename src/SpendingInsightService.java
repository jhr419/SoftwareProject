import java.util.*;
import java.time.LocalDate;

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
