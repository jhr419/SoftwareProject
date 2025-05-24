package com.shelton.ebu6403.models;

import java.time.LocalDate;

/**
 * Represents a single financial transaction, including both income and expenses.
 * <p>
 * Stores details such as category, amount, date, item name, and transaction type.
 * Also supports an optional AI-based classification field.
 * </p>
 *
 * author Haoran Jin, Haihan Sun, Jia Liu
 */
public class ExpenseRecord {

    /** Human-assigned category (e.g., Food, Travel) */
    private String category;

    /** AI-predicted category (optional) */
    private String aiCategory;

    /** Transaction amount */
    private double amount;

    /** Date of the transaction */
    private LocalDate date;

    /** Description or name of the item */
    private String itemName;

    /** Type of transaction: "income" or "expense" */
    private String transactionType;

    /** Whether to enable AI classification */
    private boolean useAI = true;

    /**
     * Constructs an ExpenseRecord with specified parameters.
     *
     * @param category         the manual category name
     * @param amount           the transaction amount
     * @param date             the transaction date
     * @param itemName         the name or label of the transaction
     * @param transactionType  "income" or "expense"
     */
    public ExpenseRecord(String category, double amount, LocalDate date, String itemName, String transactionType) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.itemName = itemName;
        this.transactionType = transactionType;
    }

    /**
     * Sets the manual category.
     *
     * @param category the new category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the manual category.
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Gets the transaction amount.
     *
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Gets the date of the transaction.
     *
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Gets the item name or description.
     *
     * @return the item name
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * Gets the transaction type.
     *
     * @return "income" or "expense"
     */
    public String getTransactionType() {
        return transactionType;
    }

    /**
     * Returns a CSV-style string representation of the record.
     *
     * @return CSV string including category, amount, date, item name, and type
     */
    @Override
    public String toString() {
        return category + "," + amount + "," + date + "," + itemName + "," + transactionType;
    }
}
