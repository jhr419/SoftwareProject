package com.shelton.ebu6403.models;

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

public class SpendingInsightServiceTest {

    @Mock
    private ApiClient mockApiClient;

    private SpendingInsightService spendingInsightService;
    private List<ExpenseRecord> testExpenses;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testExpenses = new ArrayList<>();

        // 创建测试数据
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

    @Test
    void testGenerateSpendingInsights_Success() throws Exception {
        // 设置模拟API响应
        String expectedInsight = "Test spending analysis response";
        when(mockApiClient.sendRequest(anyString())).thenReturn(expectedInsight);

        // 执行测试
        String result = spendingInsightService.generateSpendingInsights();

        // 验证结果
        assertNotNull(result);
        assertEquals(expectedInsight, result);
    }

    @Test
    void testGenerateSpendingInsights_EmptyExpenseList() throws Exception {
        // 使用空的消费记录列表创建服务
        spendingInsightService = new SpendingInsightService(new ArrayList<>(), mockApiClient);
        when(mockApiClient.sendRequest(anyString())).thenReturn("No expenses to analyze");

        String result = spendingInsightService.generateSpendingInsights();

        assertNotNull(result);
        assertEquals("No expenses to analyze", result);
    }

    @Test
    void testGenerateSpendingInsights_ApiFailure() throws Exception {
        // 模拟API调用失败的情况
        when(mockApiClient.sendRequest(anyString())).thenThrow(new RuntimeException("API Error"));

        String result = spendingInsightService.generateSpendingInsights();

        assertNotNull(result);
        assertTrue(result.contains("Unable to retrieve financial advice"));
    }
}
