package com.shelton.ebu6403.models;

import java.time.LocalDate;
import java.util.List;

/**
 * An analyzer that detects seasonal expense patterns based on traditional Chinese holidays.
 * <p>
 * Provides budget reminder prompts before holidays and checks for unusual spending spikes.
 * Useful for integrating spending context into AI-driven budgeting advice.
 * </p>
 *
 * @author Weicheng Xie, Zhifei Liu, Haihan Sun
 */
public class ChineseHolidayAnalyzer {

    /** List of all expense records to be analyzed */
    private List<ExpenseRecord> expenses;

    /**
     * Constructs a ChineseHolidayAnalyzer with the given expense data.
     *
     * @param expenses list of expense records
     */
    public ChineseHolidayAnalyzer(List<ExpenseRecord> expenses) {
        this.expenses = expenses;
    }

    /**
     * Generates a reminder message if a major holiday is within the next 7 days.
     *
     * @return a holiday reminder string; empty if no holiday is near
     */
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

    /**
     * Detects if Spring Festival expenses (Jan 20 – Feb 20) are unusually high.
     *
     * @return true if a spending spike is detected
     */
    public boolean detectSpringFestivalSpike() {
        return detectSpikeBetween(
                LocalDate.of(LocalDate.now().getYear(), 1, 20),
                LocalDate.of(LocalDate.now().getYear(), 2, 20),
                1000
        );
    }

    /**
     * Detects if May Day expenses (May 1 – May 5) are unusually high.
     *
     * @return true if a spending spike is detected
     */
    public boolean detectMayDaySpike() {
        return detectSpikeBetween(
                LocalDate.of(LocalDate.now().getYear(), 5, 1),
                LocalDate.of(LocalDate.now().getYear(), 5, 5),
                800
        );
    }

    /**
     * Detects if National Day expenses (Oct 1 – Oct 7) are unusually high.
     *
     * @return true if a spending spike is detected
     */
    public boolean detectNationalDaySpike() {
        return detectSpikeBetween(
                LocalDate.of(LocalDate.now().getYear(), 10, 1),
                LocalDate.of(LocalDate.now().getYear(), 10, 7),
                1000
        );
    }

    /**
     * Detects if Qingming Festival expenses (Mar 28 – Apr 4) are unusually high.
     *
     * @return true if a spending spike is detected
     */
    public boolean detectQingmingSpike() {
        return detectSpikeBetween(
                LocalDate.of(LocalDate.now().getYear(), 3, 28),
                LocalDate.of(LocalDate.now().getYear(), 4, 4),
                500
        );
    }

    /**
     * Detects if Children's Day expenses (May 25 – Jun 2) are unusually high.
     *
     * @return true if a spending spike is detected
     */
    public boolean detectChildrenDaySpike() {
        return detectSpikeBetween(
                LocalDate.of(LocalDate.now().getYear(), 5, 25),
                LocalDate.of(LocalDate.now().getYear(), 6, 2),
                300
        );
    }

    /**
     * Detects if Dragon Boat Festival expenses (May 30 – Jun 7) are unusually high.
     *
     * @return true if a spending spike is detected
     */
    public boolean detectDragonBoatSpike() {
        return detectSpikeBetween(
                LocalDate.of(LocalDate.now().getYear(), 5, 30),
                LocalDate.of(LocalDate.now().getYear(), 6, 7),
                500
        );
    }

    /**
     * Detects if Mid-Autumn Festival expenses (Sep 10 – Sep 17) are unusually high.
     *
     * @return true if a spending spike is detected
     */
    public boolean detectMidAutumnSpike() {
        return detectSpikeBetween(
                LocalDate.of(LocalDate.now().getYear(), 9, 10),
                LocalDate.of(LocalDate.now().getYear(), 9, 17),
                600
        );
    }

    /**
     * Core method to check if total expenses during a period exceed a threshold.
     * <p>
     * Only considers expenses within the last month and within the target holiday window.
     * </p>
     *
     * @param start     start date of the time window
     * @param end       end date of the time window
     * @param threshold the threshold amount for a spike
     * @return true if total expenses exceed the threshold
     */
    private boolean detectSpikeBetween(LocalDate start, LocalDate end, double threshold) {
        LocalDate cutoff = LocalDate.now().minusMonths(1);
        double total = 0;
        for (ExpenseRecord record : expenses) {
            LocalDate date = record.getDate();
            if (!record.getTransactionType().equalsIgnoreCase("expense")) continue;

            if ((date.isEqual(start) || date.isAfter(start)) &&
                    date.isBefore(end.plusDays(1)) &&
                    !date.isBefore(cutoff)) {
                total += record.getAmount();
            }
        }
        return total > threshold;
    }

    /**
     * Generates a seasonal spending context summary for use in AI prompts.
     * <p>
     * This method analyzes recent expense spikes and returns explanations
     * tied to known Chinese holidays.
     * </p>
     *
     * @return context string describing detected holiday spending
     */
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
