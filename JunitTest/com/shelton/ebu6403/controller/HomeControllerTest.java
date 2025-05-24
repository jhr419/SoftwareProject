package com.shelton.ebu6403.controller;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HomeControllerTest {

    @Test
    void testLoadDailyTotal() throws IOException {
        // 创建临时 CSV 文件
        File tempFile = File.createTempFile("test_expenses", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("ID,Name,Date,Amount,Category\n");
            writer.write("1,Coffee," + LocalDate.now() + ",5.50,Food\n");
            writer.write("2,Burger," + LocalDate.now() + ",8.00,Food\n");
        }

        // 测试读取
        HomeController controller = new HomeController();
        Map<LocalDate, Double> totals = controller.loadDailyTotal(tempFile.getAbsolutePath());

        assertEquals(13.50, totals.get(LocalDate.now()), 0.001);
        tempFile.deleteOnExit();
    }

    @Test
    void testReadTransactionsFromCSV() throws IOException {
        // 创建临时 CSV 文件
        File tempFile = File.createTempFile("test_transactions", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("ID,Name,Date,Amount,Category\n");
            writer.write("1,Coffee," + LocalDate.now() + ",5.50,Food\n");
            writer.write("2,Burger," + LocalDate.now() + ",8.00,Food\n");
        }

        HomeController controller = new HomeController();
        List<HomeController.Transaction> txs = controller.readTransactionsFromCSV(tempFile.getAbsolutePath());

        assertEquals(2, txs.size());
        assertEquals("Coffee", txs.get(0).getName());
        assertEquals(5.50, txs.get(0).getAmount(), 0.001);
        tempFile.deleteOnExit();
    }
}
