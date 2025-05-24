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

}
