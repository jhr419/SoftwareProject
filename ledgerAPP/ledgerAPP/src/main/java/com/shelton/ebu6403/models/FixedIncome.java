package com.shelton.ebu6403.models;

public class FixedIncome {
    private String source; // 收入来源
    private double amount; // 收入金额
    private String period; // 收入周期（如每月、每季度）

    public FixedIncome(String source, double amount, String period) {
        this.source = source;
        this.amount = amount;
        this.period = period;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return String.format("来源: %s, 金额: %.2f, 周期: %s", source, amount, period);
    }
}