package com.shelton.ebu6403;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class StatisticsController {

    @FXML
    private void openLedgerSelection() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "这里可以选择或创建账本", ButtonType.OK);
        alert.setTitle("账本选择");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

