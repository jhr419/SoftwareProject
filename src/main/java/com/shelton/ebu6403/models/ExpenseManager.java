package com.shelton.ebu6403.models;

import java.io.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

/**
 * Manages income and expense records, including budget integration and AI-powered classification.
 * <p>
 * This class loads CSV data, supports analytical summaries by category/time, and connects to
 * an external API to classify transaction categories automatically.
 * </p>
 *
 * @author Zuhao Zhang, Haoran Jin, Weicheng Xie
 */
public class ExpenseManager {

    private List<ExpenseRecord> expenses;
    private BudgetSet budgetSet;
    private ApiClient apiClient;

    /**
     * Constructs the ExpenseManager and loads data from CSV files.
     *
     * @param apiKey the API key used for classification requests
     */
    public ExpenseManager(String apiKey) {
        this.apiClient = new ApiClient(apiKey);
        this.expenses = new ArrayList<>();
        this.budgetSet = new BudgetSet();
        loadData();
    }

    /**
     * Returns the ApiClient instance.
     *
     * @return the ApiClient
     */
    public ApiClient getApiClient() {
        return this.apiClient;
    }

    /**
     * Uses an AI model to classify a transaction item name into a category.
     *
     * @param itemName the name of the transaction item
     * @param transactionType "income" or "expense"
     * @return predicted category or "Others" on failure
     */
    public String classifyWithAI(String itemName, String transactionType) {
        try {
            String[] categories = transactionType.equalsIgnoreCase("income")
                    ? new String[] {"Salary", "Investment", "Gift", "Bonus", "Others"}
                    : new String[] {"Travel", "Entertainment", "Clothing", "Education", "Transportation",
                    "Medical", "Home", "Food", "Sports", "Communication", "Others"};

            StringBuilder prompt = new StringBuilder("Classify this " + transactionType + " item: " + itemName + "\n\n");
            prompt.append("Please choose one of the following categories:\n");
            for (String category : categories) {
                prompt.append("- ").append(category).append("\n");
            }
            prompt.append("Your answer should only contain the category name, no other explanation.\n");

            String response = apiClient.sendRequest(prompt.toString());
            return response.trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Others";
        }
    }

    /**
     * Loads both income and expense records from corresponding CSV files.
     */
    public void loadData() {
        loadExpensesFromCSV("data/expenses.csv");
        loadIncomesFromCSV("data/incomes.csv");
    }

    private void loadExpensesFromCSV(String filePath) {
        loadCSVWithType(filePath, "expense");
    }

    private void loadIncomesFromCSV(String filePath) {
        loadCSVWithType(filePath, "income");
    }

    /**
     * Generalized CSV loader for income/expense files.
     *
     * @param filePath path to the CSV file
     * @param transactionType "income" or "expense"
     */
    private void loadCSVWithType(String filePath, String transactionType) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }

                String[] data = line.split(",", -1);
                if (data.length < 5) continue;

                String name = data[1];
                LocalDate date = LocalDate.parse(data[2]);
                double amount = Double.parseDouble(data[3]);
                String category = data[4];

                ExpenseRecord record = new ExpenseRecord(category, amount, date, name, transactionType);
                expenses.add(record);
            }
        } catch (IOException e) {
            System.out.println("Failed to load from " + filePath + ": " + e.getMessage());
        }
    }
    /**
     * Returns all expense and income records.
     *
     * @return list of all transaction records
     */
    public List<ExpenseRecord> getExpenses() {
        return expenses;
    }
}
