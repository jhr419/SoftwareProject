package com.shelton.ebu6403.controller;

import com.shelton.ebu6403.models.ChineseHolidayAnalyzer;
import com.shelton.ebu6403.models.ExpenseRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ChineseHolidayAnalyzerTest {

    private ChineseHolidayAnalyzer analyzer;
    private List<ExpenseRecord> expenses;

    @BeforeEach
    void setUp() {
        expenses = new ArrayList<>();
        analyzer = new ChineseHolidayAnalyzer(expenses);
    }

    @Test
    void testSpringFestivalSpike() {
        // Given
        LocalDate festivalDate = LocalDate.now().withMonth(1).withDayOfMonth(30);
        expenses.add(new ExpenseRecord("Food", 600.0, festivalDate, "New Year Dinner", "expense"));
        expenses.add(new ExpenseRecord("Gifts", 500.0, festivalDate, "New Year Gifts", "expense"));

        // When
        boolean hasSpike = analyzer.detectSpringFestivalSpike();

        // Then
        assertTrue(hasSpike, "Spring Festival spending should exceed threshold");
    }

    @Test
    void testNoHolidaySpike() {
        // Given
        LocalDate normalDay = LocalDate.now().withMonth(3).withDayOfMonth(15);
        expenses.add(new ExpenseRecord("Food", 100.0, normalDay, "Normal Dinner", "expense"));

        // When
        boolean hasSpike = analyzer.detectSpringFestivalSpike();

        // Then
        assertFalse(hasSpike, "Normal day spending should not trigger holiday spike");
    }

    @Test
    void testNationalDaySpike() {
        // Given
        LocalDate nationalDay = LocalDate.now().withMonth(10).withDayOfMonth(3);
        expenses.add(new ExpenseRecord("Travel", 800.0, nationalDay, "Holiday Trip", "expense"));
        expenses.add(new ExpenseRecord("Food", 300.0, nationalDay, "Restaurant", "expense"));

        // When
        boolean hasSpike = analyzer.detectNationalDaySpike();

        // Then
        assertTrue(hasSpike, "National Day spending should exceed threshold");
    }
}
