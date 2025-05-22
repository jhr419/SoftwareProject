package com.shelton.ebu6403.controller;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ImageView fingerprintIcon;

    @FXML private ImageView avatarView;

    @FXML
    public void initialize() {
        loadImage(avatarView, "/com/shelton/ebu6403/images/avatar.png");
    }

    private void loadImage(ImageView imageView, String resourcePath) {
        try {
            Image image = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream(resourcePath)
            ));
            imageView.setImage(image);
        } catch (NullPointerException e) {
            System.err.println("资源加载失败: " + resourcePath);
            // 设置默认占位图或处理错误
        }
    }

    @FXML
    private void handleLogin() throws IOException {
        // Close login window
        Stage currentStage = (Stage) usernameField.getScene().getWindow();
        currentStage.close();

        // Load main interface
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/com/shelton/ebu6403/views/ledger.fxml"
        ));
        Parent root = loader.load(); // 可能抛出 IOException

        Stage mainStage = new Stage();
        mainStage.setScene(new Scene(root, 800, 600));
        mainStage.setTitle("Smart Ledger System");

        // Set window icon（可能抛出 NullPointerException）
        mainStage.getIcons().add(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(
                        "/com/shelton/ebu6403/images/icon.png"
                )
        )));

        mainStage.show();
    }


    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}