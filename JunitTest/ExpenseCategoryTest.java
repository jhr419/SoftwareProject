import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.*;

public class ExpenseCategoryTest {

    private ExpenseCategory category;
    private final String TEST_FILE_PATH = "categories.csv";

    @Before
    public void setUp() {
        // 初始化前删除旧的测试文件，防止测试冲突
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        category = new ExpenseCategory();
    }

    @After
    public void tearDown() {
        // 测试完成后删除测试文件
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void saveCategoriesToFile() {
        category.addCategory("Food", 100.0);
        category.addCategory("Transport", 50.0);

        File file = new File(TEST_FILE_PATH);
        assertTrue("File should exist after saving", file.exists());
    }

    @Test
    public void addCategory() {
        category.addCategory("Entertainment", 200.0);
        Map<String, Double> categories = category.getCategories();
        assertTrue(categories.containsKey("Entertainment"));
        assertEquals(200.0, categories.get("Entertainment"), 0.001);
    }

    @Test
    public void modifyCategory() {
        category.addCategory("Shopping", 300.0);
        category.modifyCategory("Shopping", 400.0);
        assertEquals(400.0, category.getCategories().get("Shopping"), 0.001);
    }

    @Test
    public void removeCategory() {
        category.addCategory("Travel", 500.0);
        category.removeCategory("Travel");
        assertFalse(category.getCategories().containsKey("Travel"));
    }

    @Test
    public void getCategories() {
        category.addCategory("Books", 75.5);
        Map<String, Double> map = category.getCategories();
        assertEquals(1, map.size());
        assertTrue(map.containsKey("Books"));
        assertEquals(75.5, map.get("Books"), 0.001);
    }
}
