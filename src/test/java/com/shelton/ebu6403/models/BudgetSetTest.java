package com.shelton.ebu6403.models;

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
    void testAddExpense() {
        // Given
        double expenseAmount = 500.0;
        budgetSet.setBudget(CATEGORY, CURRENT_MONTH, BUDGET_AMOUNT);

        // When
        budgetSet.addExpense(CATEGORY, TODAY, expenseAmount);

        // Then
        double progress = budgetSet.getBudgetProgress(CATEGORY, CURRENT_MONTH);
        assertEquals(0.5, progress); // 500/1000 = 0.5
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
    void testRemoveBudget() {
        // Given
        budgetSet.setBudget(CATEGORY, CURRENT_MONTH, BUDGET_AMOUNT);

        // When
        boolean removed = budgetSet.removeBudget(CATEGORY, CURRENT_MONTH);

        // Then
        assertTrue(removed);
        assertTrue(budgetSet.getBudgetsByCategory(CATEGORY).isEmpty());
    }

    @Test
    void testRemoveNonexistentBudget() {
        // When
        boolean removed = budgetSet.removeBudget("NonexistentCategory", CURRENT_MONTH);

        // Then
        assertFalse(removed);
    }

    @Test
    void testBudgetProgressWithNoExpenses() {
        // Given
        budgetSet.setBudget(CATEGORY, CURRENT_MONTH, BUDGET_AMOUNT);

        // When
        double progress = budgetSet.getBudgetProgress(CATEGORY, CURRENT_MONTH);

        // Then
        assertEquals(0.0, progress);
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
