package com.shelton.ebu6403;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class LedgerRecord {
    private final SimpleStringProperty date;
    private final SimpleStringProperty description;
    private final SimpleDoubleProperty amount;

    public LedgerRecord(String date, String description, double amount) {
        this.date = new SimpleStringProperty(date);
        this.description = new SimpleStringProperty(description);
        this.amount = new SimpleDoubleProperty(amount);
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public SimpleDoubleProperty amountProperty() {
        return amount;
    }
}

