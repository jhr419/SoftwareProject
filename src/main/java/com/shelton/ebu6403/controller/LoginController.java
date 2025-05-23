package com.shelton.ebu6403.controller;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.Objects;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ImageView fingerprintIcon;
    @FXML private Label loginMessage;
    @FXML private TextField passwordVisibleField;
    @FXML private ImageView eyeIcon;
    private boolean passwordVisible = false;

    @FXML private ImageView avatarView;

    @FXML
    public void initialize() {
        loadImage(avatarView, "/com/shelton/ebu6403/images/icon.png");
        loadImage(eyeIcon, "/com/shelton/ebu6403/images/hide.png");

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
        String username = usernameField.getText().trim();
        String password = passwordVisible ? passwordVisibleField.getText().trim()
                : passwordField.getText().trim();

        boolean loginSuccess = checkCredentials(username, password);

        if (!loginSuccess) {
            loginMessage.setText("Login failed. Hint: It’s your school");
            return;
        }

        loginMessage.setText(""); // 清空提示
        Stage currentStage = (Stage) usernameField.getScene().getWindow();
        currentStage.close();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/shelton/ebu6403/views/ledger.fxml"));
        Parent root = loader.load();
        Stage mainStage = new Stage();
        mainStage.setScene(new Scene(root, 800, 600));
        mainStage.setTitle("Smart Ledger System");
        mainStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/shelton/ebu6403/images/icon.png"))));
        mainStage.show();
    }

    private boolean checkCredentials(String user, String pass) {
        File file = new File("data/users.csv");
        if (!file.exists()) return user.equals("BUPT") && pass.equals("BUPT");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length == 2 && parts[0].equals(user) && parts[1].equals(pass)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 没写入，默认
        return user.equals("BUPT") && pass.equals("BUPT");
    }


    @FXML
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            passwordVisibleField.setText(passwordField.getText());
            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            loadImage(eyeIcon, "/com/shelton/ebu6403/images/show.png");
        } else {
            passwordField.setText(passwordVisibleField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
            loadImage(eyeIcon, "/com/shelton/ebu6403/images/hide.png");
        }
    }

    @FXML
    private void handleCreateAccount() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create Account");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        TextField newUserField = new TextField();
        PasswordField newPassField = new PasswordField();
        PasswordField confirmPassField = new PasswordField();

        grid.addRow(0, new Label("Username:"), newUserField);
        grid.addRow(1, new Label("Password:"), newPassField);
        grid.addRow(2, new Label("Confirm Password:"), confirmPassField);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String user = newUserField.getText().trim();
                String pass = newPassField.getText().trim();
                String confirm = confirmPassField.getText().trim();

                if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                    showInlineAlert("All fields required.");
                    return null;
                }

                if (!pass.equals(confirm)) {
                    showInlineAlert("Passwords do not match.");
                    return null;
                }

                if (isUsernameExists(user)) {
                    showInlineAlert("Username already exists.");
                    return null;
                }

                saveUserToFile(user, pass);

            }
            return null;
        });

        dialog.showAndWait();
    }


    private void saveUserToFile(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/users.csv", true))) {
            writer.newLine();
            writer.write(username + "," + password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showInlineAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private boolean isUsernameExists(String username) {
        File file = new File("data/users.csv");
        if (!file.exists()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 1 && parts[0].equalsIgnoreCase(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}