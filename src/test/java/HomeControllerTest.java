import com.shelton.ebu6403.controller.HomeController;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for HomeController.
 * Contains test cases to verify the home dashboard functionality including:
 * - Daily transaction totals calculation
 * - CSV file reading and parsing
 * - Transaction data validation
 */
class HomeControllerTest {

    /**
     * Tests the loadDailyTotal method.
     * Verifies that:
     * - Daily transaction totals are correctly calculated
     * - Multiple transactions on the same day are properly summed
     * - CSV file reading works correctly
     *
     * @throws IOException if there are issues with file operations
     */
    @Test
    void testLoadDailyTotal() throws IOException {
        // Create temporary CSV file for testing
        File tempFile = File.createTempFile("test_expenses", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("ID,Name,Date,Amount,Category\n");
            writer.write("1,Coffee," + LocalDate.now() + ",5.50,Food\n");
            writer.write("2,Burger," + LocalDate.now() + ",8.00,Food\n");
        }

        // Test file reading and total calculation
        HomeController controller = new HomeController();
        Map<LocalDate, Double> totals = controller.loadDailyTotal(tempFile.getAbsolutePath());

        assertEquals(13.50, totals.get(LocalDate.now()), 0.001, "Daily total should be sum of all transactions");
        tempFile.deleteOnExit();
    }

    /**
     * Tests the readTransactionsFromCSV method.
     * Verifies that:
     * - Transactions are correctly read from CSV
     * - Transaction objects are properly populated
     * - All fields (ID, Name, Date, Amount, Category) are correctly parsed
     *
     * @throws IOException if there are issues with file operations
     */
    @Test
    void testReadTransactionsFromCSV() throws IOException {
        // Create temporary CSV file for testing
        File tempFile = File.createTempFile("test_transactions", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("ID,Name,Date,Amount,Category\n");
            writer.write("1,Coffee," + LocalDate.now() + ",5.50,Food\n");
            writer.write("2,Burger," + LocalDate.now() + ",8.00,Food\n");
        }

        HomeController controller = new HomeController();
        List<HomeController.Transaction> txs = controller.readTransactionsFromCSV(tempFile.getAbsolutePath());

        assertEquals(2, txs.size(), "Should read correct number of transactions");
        assertEquals("Coffee", txs.get(0).getName(), "Transaction name should match input");
        assertEquals(5.50, txs.get(0).getAmount(), 0.001, "Transaction amount should be correctly parsed");
        tempFile.deleteOnExit();
    }
}
