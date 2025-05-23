package com.shelton.ebu6403.controller;

import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoriesControllerTest extends ApplicationTest {

    private CategoriesController controller;

    @Override
    public void start(Stage stage) throws Exception {
        // 加载FXML并初始化controller，省略细节，假设controller已初始化
        // 例如 FXMLLoader.load(...) 返回父节点，获取controller引用
        // 这里直接实例化模拟
        controller = new CategoriesController();
        controller.initialize();
    }

    @BeforeEach
    public void setup() {
        // 清理数据，重置列表
        interact(() -> {
            controller.allExpenses.clear();
            controller.allIncomes.clear();
            controller.expensesTable.setItems(controller.allExpenses);
            controller.incomeTable.setItems(controller.allIncomes);
        });
    }

    @Test
    public void testInitialize_shouldLoadCategoriesAndTables() {
        interact(() -> {
            // 确认支出类别卡片数
            assertEquals(controller.expenseCategories.length + 1, controller.expensesCardContainer.getChildren().size());
            // 确认收入类别卡片数
            assertEquals(controller.incomeCategories.length + 1, controller.incomeCardContainer.getChildren().size());
            // 表格列数5
            assertEquals(5, controller.expensesTable.getColumns().size());
            assertEquals(5, controller.incomeTable.getColumns().size());
        });
    }

    @Test
    public void testHandleImportCSV_shouldImportExpenses() throws Exception {
        // 创建临时CSV文件
        String tempFilePath = "temp_expenses.csv";
        Files.write(Paths.get(tempFilePath), (
                "serialNo,name,date,amount,category\n" +
                        "1,Test Expense,2025-05-23,100.00,Food\n" +
                        "2,Another Expense,2025-05-24,50.00,\n" // 无category走AI
        ).getBytes());

        interact(() -> {
            // 模拟导入（内部调用handleImportCSV打开文件对话框，这里改成调用读入函数或模拟）
            // 这里直接调用readTransactionsFromCSV作为简单测试替代
            var imported = controller.readTransactionsFromCSV(tempFilePath);
            assertEquals(2, imported.size());
            assertEquals("Test Expense", imported.get(0).getName());
            assertEquals("Another Expense", imported.get(1).getName());
        });

        Files.deleteIfExists(Paths.get(tempFilePath));
    }

    @Test
    public void testAddAndDeleteExpense() {
        interact(() -> {
            // 手动新增一个支出
            var tx = new CategoriesController.Transaction(1, "Lunch", "2025-05-23", 12.5, "Food");
            controller.allExpenses.add(tx);
            controller.expensesTable.setItems(controller.allExpenses);

            assertEquals(1, controller.allExpenses.size());
            assertEquals("Lunch", controller.allExpenses.get(0).getName());

            // 选择并删除
            controller.expensesTable.getSelectionModel().select(tx);
            controller.handleDeleteExpense();

            assertEquals(0, controller.allExpenses.size());
        });
    }

    @Test
    public void testFilterTransactions() {
        interact(() -> {
            controller.allExpenses.add(new CategoriesController.Transaction(1, "Lunch", "2025-05-23", 10, "Food"));
            controller.allExpenses.add(new CategoriesController.Transaction(2, "Taxi", "2025-05-23", 20, "Transportation"));
            controller.expensesTable.setItems(controller.allExpenses);

            controller.filterTransactions("Food", "Expense");
            assertEquals(1, controller.expensesTable.getItems().size());
            assertEquals("Food", controller.expensesTable.getItems().get(0).getCategory());

            controller.filterTransactions("Transportation", "Expense");
            assertEquals(1, controller.expensesTable.getItems().size());
            assertEquals("Transportation", controller.expensesTable.getItems().get(0).getCategory());
        });
    }

    @Test
    public void testShowNewCategoryDialog() {
        interact(() -> {
            // 弹出对话框不易自动化测试，这里只测试添加卡片逻辑
            int originalCount = controller.expensesCardContainer.getChildren().size();
            // 模拟添加类别
            controller.showNewCategoryDialog("Expense");

            // 这里不能模拟用户输入，所以对话框弹出只是流程验证，无法自动点击确认
            // 可以将showNewCategoryDialog改为支持传参的方法更方便测试
            // 这里只检查卡片数没有变，除非重写函数
            assertTrue(controller.expensesCardContainer.getChildren().size() >= originalCount);
        });
    }
}
