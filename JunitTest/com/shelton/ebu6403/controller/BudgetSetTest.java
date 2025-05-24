package com.shelton.ebu6403.controller;

import com.shelton.ebu6403.models.BudgetSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BudgetSetTest {
    private BudgetSet budgetSet;
    private final String CATEGORY = "Food";
    private final YearMonth CURRENT_MONTH = YearMonth.now();
    private final LocalDate TODAY = LocalDate.now();
    private final double BUDGET_AMOUNT = 1000.0;

    @BeforeEach
    void setUp() {
        budgetSet = new BudgetSet();
    }

    @Test
    void testSetBudget() {
        // When
        budgetSet.setBudget(CATEGORY, CURRENT_MONTH, BUDGET_AMOUNT);

        // Then
        Map<YearMonth, Double> categoryBudgets = budgetSet.getBudgetsByCategory(CATEGORY);
        assertNotNull(categoryBudgets);
        assertEquals(BUDGET_AMOUNT, categoryBudgets.get(CURRENT_MONTH));
    }

    @Test
    void testGetAllBudgets() {
        // Given
        budgetSet.setBudget(CATEGORY, CURRENT_MONTH, BUDGET_AMOUNT);

        // When
        Map<String, Map<YearMonth, Double>> allBudgets = budgetSet.getAllBudgets();

        // Then
        assertNotNull(allBudgets);
        assertTrue(allBudgets.containsKey(CATEGORY));
        assertEquals(BUDGET_AMOUNT, allBudgets.get(CATEGORY).get(CURRENT_MONTH));
    }

    @Test
    void testGetBudgetsByCategory() {
        // Given
        budgetSet.setBudget(CATEGORY, CURRENT_MONTH, BUDGET_AMOUNT);

        // When
        Map<YearMonth, Double> categoryBudgets = budgetSet.getBudgetsByCategory(CATEGORY);

        // Then
        assertNotNull(categoryBudgets);
        assertEquals(BUDGET_AMOUNT, categoryBudgets.get(CURRENT_MONTH));
    }


    @Test
    void testRemoveNonexistentBudget() {
        // When
        boolean removed = budgetSet.removeBudget("NonexistentCategory", CURRENT_MONTH);

        // Then
        assertFalse(removed);
    }


    @Test
    void testBudgetProgressExceedingBudget() {
        // Given
        budgetSet.setBudget(CATEGORY, CURRENT_MONTH, BUDGET_AMOUNT);
        budgetSet.addExpense(CATEGORY, TODAY, BUDGET_AMOUNT * 1.5);

        // When
        double progress = budgetSet.getBudgetProgress(CATEGORY, CURRENT_MONTH);

        // Then
        assertEquals(1.0, progress); // Progress should be capped at 1.0
    }

    @Test
    void testBudgetProgressWithNoBudget() {
        // When
        double progress = budgetSet.getBudgetProgress("NonexistentCategory", CURRENT_MONTH);

        // Then
        assertEquals(0.0, progress);
    }
}
