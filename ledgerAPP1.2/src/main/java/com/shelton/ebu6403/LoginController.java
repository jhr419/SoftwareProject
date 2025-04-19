// 路径: src/main/java/com/shelton/ebu6403/LoginController.java
package com.shelton.ebu6403;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        try {
            // 关闭登录窗口
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();

            // 加载主界面
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ledger.fxml"));
            Parent root = loader.load();

            Stage mainStage = new Stage();
            mainStage.setScene(new Scene(root, 800, 600));
            mainStage.setTitle("Ledger Main");
            mainStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
