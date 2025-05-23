package com.shelton.ebu6403.models;

import java.time.LocalDate;
import java.util.List;

public class ChineseHolidayAnalyzer {

    private List<ExpenseRecord> expenses;

    public ChineseHolidayAnalyzer(List<ExpenseRecord> expenses) {
        this.expenses = expenses;
    }
    // 添加：判断是否临近节日（提前7天提醒）

    public String getUpcomingHolidayReminder() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();

        if (!now.isBefore(LocalDate.of(year, 1, 13)) && now.isBefore(LocalDate.of(year, 1, 20))) {
            return "Lunar New Year is approaching. Consider adjusting your budget for categories like Food & Gifts.";
        } else if (!now.isBefore(LocalDate.of(year, 3, 28)) && now.isBefore(LocalDate.of(year, 4, 4))) {
            return "Qingming Festival is approaching. You may prepare for travel, flowers, or memorial spending.";
        } else if (!now.isBefore(LocalDate.of(year, 4, 24)) && now.isBefore(LocalDate.of(year, 5, 1))) {
            return "May Day Holiday is approaching. Consider planning your Travel or Entertainment budget.";
        } else if (!now.isBefore(LocalDate.of(year, 5, 23)) && now.isBefore(LocalDate.of(year, 6, 1))) {
            return "Children's Day is approaching. Consider budgeting for gifts, toys or family outings.";
        } else if (!now.isBefore(LocalDate.of(year, 5, 30)) && now.isBefore(LocalDate.of(year, 6, 7))) {
            return "Dragon Boat Festival is approaching. Consider food and activity expenses.";
        } else if (!now.isBefore(LocalDate.of(year, 9, 10)) && now.isBefore(LocalDate.of(year, 9, 17))) {
            return "Mid-Autumn Festival is coming. Prepare for family dinners or gift purchases.";
        } else if (!now.isBefore(LocalDate.of(year, 9, 24)) && now.isBefore(LocalDate.of(year, 10, 1))) {
            return "National Day Holiday is approaching. Plan ahead for Home or Travel spending.";
        }

        return "";
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
    // 判断清明节支出是否异常高（3月28日 - 4月4日）
    public boolean detectQingmingSpike() {
        return detectSpikeBetween(LocalDate.of(LocalDate.now().getYear(), 3, 28),
                LocalDate.of(LocalDate.now().getYear(), 4, 4),
                500); // 阈值可调整
    }

    // 判断儿童节支出是否异常高（5月25日 - 6月2日）
    public boolean detectChildrenDaySpike() {
        return detectSpikeBetween(LocalDate.of(LocalDate.now().getYear(), 5, 25),
                LocalDate.of(LocalDate.now().getYear(), 6, 2),
                300);
    }

    // 判断端午节支出是否异常高（5月30日 - 6月7日）
    public boolean detectDragonBoatSpike() {
        return detectSpikeBetween(LocalDate.of(LocalDate.now().getYear(), 5, 30),
                LocalDate.of(LocalDate.now().getYear(), 6, 7),
                500);
    }

    // 判断中秋节支出是否异常高（9月10日 - 9月17日）
    public boolean detectMidAutumnSpike() {
        return detectSpikeBetween(LocalDate.of(LocalDate.now().getYear(), 9, 10),
                LocalDate.of(LocalDate.now().getYear(), 9, 17),
                600);
    }

    // 公共方法：检测某个时间段内是否总支出超过阈值
    private boolean detectSpikeBetween(LocalDate start, LocalDate end, double threshold) {
        LocalDate cutoff = LocalDate.now().minusMonths(1); // 限定只看过去一个月
        double total = 0;
        for (ExpenseRecord record : expenses) {
            LocalDate date = record.getDate();
            if (!record.getTransactionType().equalsIgnoreCase("expense")) continue;

            // 限定日期必须在过去1个月内，且落入节日时间段
            if ((date.isEqual(start) || date.isAfter(start)) &&
                    date.isBefore(end.plusDays(1)) &&
                    !date.isBefore(cutoff)) {

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
        if (detectQingmingSpike()) {
            sb.append("Spending around early April appears elevated, which may relate to Qingming Festival.\n");
        }
        if (detectMayDaySpike()) {
            sb.append("Spending around early May is notable. May Day Holiday may contribute to this.\n");
        }
        if (detectChildrenDaySpike()) {
            sb.append("Spending in late May may be related to Children's Day preparations or celebrations.\n");
        }
        if (detectDragonBoatSpike()) {
            sb.append("Recent food or cultural activity expenses may indicate Dragon Boat Festival spending.\n");
        }
        if (detectMidAutumnSpike()) {
            sb.append("Elevated food or gift spending in September may relate to the Mid-Autumn Festival.\n");
        }
        if (detectNationalDaySpike()) {
            sb.append("A spike in expenses around early October suggests spending during National Day Holiday.\n");
        }

        return sb.toString();
    }

}
