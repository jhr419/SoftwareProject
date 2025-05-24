package com.shelton.ebu6403.controller;

import com.shelton.ebu6403.models.ExpenseRecord;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ExpenseRecord.
 * Contains test cases to verify the functionality of expense records including:
 * - Record creation and field validation
 * - AI category management
 * - String representation
 * - AI usage flag management
 */
public class ExpenseRecordTest {

    /**
     * Tests the creation of an expense record.
     * Verifies that all fields are correctly initialized and can be retrieved:
     * - Category
     * - Amount
     * - Date
     * - Item name
     * - Transaction type
     */
    @Test
    void testCreateExpenseRecord() {
        // Given
        String category = "Food";
        double amount = 100.0;
        LocalDate date = LocalDate.now();
        String itemName = "Lunch";
        String transactionType = "expense";

        // When
        ExpenseRecord record = new ExpenseRecord(category, amount, date, itemName, transactionType);

        // Then - Verify all fields are correctly set
        assertEquals(category, record.getCategory());
        assertEquals(amount, record.getAmount());
        assertEquals(date, record.getDate());
        assertEquals(itemName, record.getItemName());
        assertEquals(transactionType, record.getTransactionType());
    }

    /**
     * Tests setting and getting the AI-suggested category.
     * Verifies that:
     * - AI category can be set
     * - AI category can be retrieved
     * - Retrieved value matches the set value
     */
    @Test
    void testSetAndGetAICategory() {
        // Given
        ExpenseRecord record = new ExpenseRecord("Food", 100.0, LocalDate.now(), "Dinner", "expense");
        String aiCategory = "Restaurant";

        // When
        record.setAiCategory(aiCategory);

        // Then - Verify AI category is correctly stored
        assertEquals(aiCategory, record.getAiCategory());
    }

    /**
     * Tests the string representation of an expense record.
     * Verifies that toString() returns the correct CSV format:
     * category,amount,date,itemName,transactionType
     */
    @Test
    void testToString() {
        // Given
        String category = "Food";
        double amount = 100.0;
        LocalDate date = LocalDate.now();
        String itemName = "Lunch";
        String transactionType = "expense";
        ExpenseRecord record = new ExpenseRecord(category, amount, date, itemName, transactionType);

        // When
        String result = record.toString();

        // Then - Verify CSV string format
        String expected = category + "," + amount + "," + date + "," + itemName + "," + transactionType;
        assertEquals(expected, result);
    }

    /**
     * Tests the default value of the AI usage flag.
     * Verifies that:
     * - New records have AI usage enabled by default
     * - The getUseAI method returns true for new records
     */
    @Test
    void testDefaultUseAIValue() {
        // Given
        ExpenseRecord record = new ExpenseRecord("Food", 100.0, LocalDate.now(), "Dinner", "expense");

        // When
        boolean useAI = record.getUseAI();

        // Then - Verify default AI usage is enabled
        assertTrue(useAI); // Default value should be true
    }
}
