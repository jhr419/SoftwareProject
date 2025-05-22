package com.shelton.ebu6403.models;

import java.time.LocalDate;
import java.util.List;

public class ChineseHolidayAnalyzer {

    private List<ExpenseRecord> expenses;

    public ChineseHolidayAnalyzer(List<ExpenseRecord> expenses) {
        this.expenses = expenses;
    }

    // 判断春节支出是否异常高（1月20日 - 2月20日）
    public boolean detectSpringFestivalSpike() {
        return detectSpikeBetween(LocalDate.of(LocalDate.now().getYear(), 1, 20),
                LocalDate.of(LocalDate.now().getYear(), 2, 20),
                1000);
    }

    // 判断五一劳动节支出是否异常高（5月1日 - 5月5日）
    public boolean detectMayDaySpike() {
        return detectSpikeBetween(LocalDate.of(LocalDate.now().getYear(), 5, 1),
                LocalDate.of(LocalDate.now().getYear(), 5, 5),
                800);
    }

    // 判断国庆节支出是否异常高（10月1日 - 10月7日）
    public boolean detectNationalDaySpike() {
        return detectSpikeBetween(LocalDate.of(LocalDate.now().getYear(), 10, 1),
                LocalDate.of(LocalDate.now().getYear(), 10, 7),
                1000);
    }

    // 公共方法：检测某个时间段内是否总支出超过阈值
    private boolean detectSpikeBetween(LocalDate start, LocalDate end, double threshold) {
        double total = 0;
        for (ExpenseRecord record : expenses) {
            LocalDate date = record.getDate();
            if (!record.getTransactionType().equalsIgnoreCase("expense")) continue;
            if ((date.isEqual(start) || date.isAfter(start)) && date.isBefore(end.plusDays(1))) {
                total += record.getAmount();
            }
        }
        return total > threshold;
    }

    // 生成用于提示模型的自然语言上下文
    public String generateSeasonalContext() {
        StringBuilder sb = new StringBuilder();
        if (detectSpringFestivalSpike()) {
            sb.append("Recent expenses during late January and early February seem high, possibly related to Chinese Spring Festival.\n");
        }
        if (detectMayDaySpike()) {
            sb.append("Spending around early May is notable. May Day Holiday may contribute to this.\n");
        }
        if (detectNationalDaySpike()) {
            sb.append("A spike in expenses around early October suggests spending during National Day Holiday.\n");
        }
        return sb.toString();
    }
}
