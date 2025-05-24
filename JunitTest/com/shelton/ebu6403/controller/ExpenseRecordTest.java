package com.shelton.ebu6403.controller;

import com.shelton.ebu6403.models.ExpenseRecord;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class ExpenseRecordTest {

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

        // Then
        assertEquals(category, record.getCategory());
        assertEquals(amount, record.getAmount());
        assertEquals(date, record.getDate());
        assertEquals(itemName, record.getItemName());
        assertEquals(transactionType, record.getTransactionType());
    }

    @Test
    void testSetAndGetAICategory() {
        // Given
        ExpenseRecord record = new ExpenseRecord("Food", 100.0, LocalDate.now(), "Dinner", "expense");
        String aiCategory = "Restaurant";

        // When
        record.setAiCategory(aiCategory);

        // Then
        assertEquals(aiCategory, record.getAiCategory());
    }

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

        // Then
        String expected = category + "," + amount + "," + date + "," + itemName + "," + transactionType;
        assertEquals(expected, result);
    }

    @Test
    void testDefaultUseAIValue() {
        // Given
        ExpenseRecord record = new ExpenseRecord("Food", 100.0, LocalDate.now(), "Dinner", "expense");

        // When
        boolean useAI = record.getUseAI();

        // Then
        assertTrue(useAI); // Default value should be true
    }
}
