package com.shelton.ebu6403.controller;

import com.shelton.ebu6403.models.ApiClient;
import com.shelton.ebu6403.models.ExpenseManager;
import com.shelton.ebu6403.models.ExpenseRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExpenseManagerTest {

    private ExpenseManager expenseManager;

    @Mock
    private ApiClient mockApiClient;

    private final LocalDate TODAY = LocalDate.now();
    private final String TEST_EXPENSE_CATEGORY = "Food";
    private final String TEST_INCOME_CATEGORY = "Salary";
    private final double TEST_AMOUNT = 100.0;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Create a spy to avoid actual file operations during tests
        expenseManager = spy(new ExpenseManager("test-api-key"));
        // Inject mock ApiClient
        doReturn(mockApiClient).when(expenseManager).getApiClient();
        // Avoid actual file operations
        doNothing().when(expenseManager).loadData();
    }

    @Test
    void testDisplayExpenses() {
        // Given
        ExpenseRecord expense = new ExpenseRecord(TEST_EXPENSE_CATEGORY, TEST_AMOUNT, TODAY, "Test", "expense");
        expenseManager.getExpenses().add(expense);

        // When/Then
        assertDoesNotThrow(() -> expenseManager.displayExpenses());
    }

    @Test
    void testDisplayCategoryExpenses() {
        // Given
        ExpenseRecord expense = new ExpenseRecord(TEST_EXPENSE_CATEGORY, TEST_AMOUNT, TODAY, "Test", "expense");
        expenseManager.getExpenses().add(expense);

        // When/Then
        assertDoesNotThrow(() -> expenseManager.displayCategoryExpenses());
    }

    @Test
    void testDisplayCategoryAndTimeExpenses() {
        // Given
        ExpenseRecord expense = new ExpenseRecord(TEST_EXPENSE_CATEGORY, TEST_AMOUNT, TODAY, "Test", "expense");
        expenseManager.getExpenses().add(expense);

        // When/Then
        assertDoesNotThrow(() -> expenseManager.displayCategoryAndTimeExpenses());
    }

    @Test
    void testDisplayCategoryAndMonthExpenses() {
        // Given
        ExpenseRecord expense = new ExpenseRecord(TEST_EXPENSE_CATEGORY, TEST_AMOUNT, TODAY, "Test", "expense");
        expenseManager.getExpenses().add(expense);

        // When/Then
        assertDoesNotThrow(() -> expenseManager.displayCategoryAndMonthExpenses());
    }
}
