import com.shelton.ebu6403.models.BudgetSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BudgetSet.
 * Contains test cases to verify budget management functionality including:
 * - Setting and retrieving budgets
 * - Adding expenses and tracking budget progress
 * - Managing budgets across different categories and time periods
 */
public class BudgetSetTest {
    private BudgetSet budgetSet;
    private final String CATEGORY = "Food";
    private final YearMonth CURRENT_MONTH = YearMonth.now();
    private final LocalDate TODAY = LocalDate.now();
    private final double BUDGET_AMOUNT = 1000.0;

    /**
     * Sets up the test environment before each test.
     * Initializes a new BudgetSet instance.
     */
    @BeforeEach
    void setUp() {
        budgetSet = new BudgetSet();
    }

    /**
     * Tests setting a budget for a specific category and month.
     * Verifies that the budget is correctly stored and can be retrieved.
     */
    @Test
    void testSetBudget() {
        // When
        budgetSet.setBudget(CATEGORY, CURRENT_MONTH, BUDGET_AMOUNT);

        // Then
        Map<YearMonth, Double> categoryBudgets = budgetSet.getBudgetsByCategory(CATEGORY);
        assertNotNull(categoryBudgets);
        assertEquals(BUDGET_AMOUNT, categoryBudgets.get(CURRENT_MONTH));
    }

    /**
     * Tests retrieving all budgets across all categories.
     * Verifies that budgets are correctly stored and can be retrieved globally.
     */
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

    /**
     * Tests retrieving budgets for a specific category.
     * Verifies that category-specific budgets can be correctly retrieved.
     */
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

    /**
     * Tests attempting to remove a non-existent budget.
     * Verifies that the system handles non-existent budget removal gracefully.
     */
    @Test
    void testRemoveNonexistentBudget() {
        // When
        boolean removed = budgetSet.removeBudget("NonexistentCategory", CURRENT_MONTH);

        // Then
        assertFalse(removed);
    }

    /**
     * Tests budget progress calculation when expenses exceed the budget.
     * Verifies that the progress is capped at 1.0 (100%) when expenses exceed the budget.
     */
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

    /**
     * Tests budget progress calculation for a non-existent budget.
     * Verifies that the system returns 0.0 progress for categories without a budget.
     */
    @Test
    void testBudgetProgressWithNoBudget() {
        // When
        double progress = budgetSet.getBudgetProgress("NonexistentCategory", CURRENT_MONTH);

        // Then
        assertEquals(0.0, progress);
    }
}
