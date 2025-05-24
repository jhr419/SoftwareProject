package com.shelton.ebu6403.controller;

import com.shelton.ebu6403.models.ApiClient;
import com.shelton.ebu6403.models.ExpenseRecord;
import com.shelton.ebu6403.models.SpendingInsightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Test class for SpendingInsightService.
 * Contains test cases to verify the AI-powered spending analysis functionality including:
 * - Successful insight generation
 * - Empty expense list handling
 * - API failure scenarios
 * Uses Mockito for API client simulation.
 */
public class SpendingInsightServiceTest {

    @Mock
    private ApiClient mockApiClient;

    private SpendingInsightService spendingInsightService;
    private List<ExpenseRecord> testExpenses;

    /**
     * Sets up the test environment before each test.
     * Initializes:
     * - Mock API client
     * - Test expense records
     * - SpendingInsightService instance with test data
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testExpenses = new ArrayList<>();

        // Create test expense records
        testExpenses.add(new ExpenseRecord(
            "Food",         // category
            100.0,         // amount
            LocalDate.now(),// date
            "Lunch",       // itemName
            "expense"      // transactionType
        ));
        testExpenses.add(new ExpenseRecord(
            "Transport",    // category
            50.0,          // amount
            LocalDate.now(),// date
            "Taxi",        // itemName
            "expense"      // transactionType
        ));

        spendingInsightService = new SpendingInsightService(testExpenses, mockApiClient);
    }

    /**
     * Tests successful generation of spending insights.
     * Verifies that:
     * - API call returns expected response
     * - Response is properly processed
     * - Result matches expected insight
     *
     * @throws Exception if there is an error during API communication
     */
    @Test
    void testGenerateSpendingInsights_Success() throws Exception {
        // Setup mock API response
        String expectedInsight = "Test spending analysis response";
        when(mockApiClient.sendRequest(anyString())).thenReturn(expectedInsight);

        // Execute test
        String result = spendingInsightService.generateSpendingInsights();

        // Verify results
        assertNotNull(result);
        assertEquals(expectedInsight, result);
    }

    /**
     * Tests insight generation with empty expense list.
     * Verifies that:
     * - Empty expense list is handled gracefully
     * - Appropriate message is returned
     * - No errors are thrown
     *
     * @throws Exception if there is an error during API communication
     */
    @Test
    void testGenerateSpendingInsights_EmptyExpenseList() throws Exception {
        // Create service with empty expense list
        spendingInsightService = new SpendingInsightService(new ArrayList<>(), mockApiClient);
        when(mockApiClient.sendRequest(anyString())).thenReturn("No expenses to analyze");

        String result = spendingInsightService.generateSpendingInsights();

        assertNotNull(result);
        assertEquals("No expenses to analyze", result);
    }

    /**
     * Tests insight generation when API call fails.
     * Verifies that:
     * - API failures are handled gracefully
     * - Error message is properly formatted
     * - Service continues to function despite API failure
     *
     * @throws Exception if there is an error during API communication
     */
    @Test
    void testGenerateSpendingInsights_ApiFailure() throws Exception {
        // Simulate API failure
        when(mockApiClient.sendRequest(anyString())).thenThrow(new RuntimeException("API Error"));

        String result = spendingInsightService.generateSpendingInsights();

        assertNotNull(result);
        assertTrue(result.contains("Unable to retrieve financial advice"));
    }
}
