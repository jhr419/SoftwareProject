package com.shelton.ebu6403.controller;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.Objects;

/**
 * Controller class for handling user login functionality.
 * <p>
 * Manages user authentication, account creation, and password visibility.
 * </p>
 * author Zuhao Zhang, Jia LIU, Haihan Sun
 */
public class LoginController {
    /** Username input field */
    @FXML private TextField usernameField;
    /** Password input field (hidden) */
    @FXML private PasswordField passwordField;
    /** Fingerprint icon for biometric login */
    @FXML private ImageView fingerprintIcon;
    /** Label for displaying login status messages */
    @FXML private Label loginMessage;
    /** Password input field (visible) */
    @FXML private TextField passwordVisibleField;
    /** Icon for toggling password visibility */
    @FXML private ImageView eyeIcon;
    /** Flag indicating whether password is visible */
    private boolean passwordVisible = false;
    /** User avatar display */
    @FXML private ImageView avatarView;

    /**
     * Initializes the controller.
     * Loads necessary images for the interface.
     */
    @FXML
    public void initialize() {
        loadImage(avatarView, "/com/shelton/ebu6403/images/icon.png");
        loadImage(eyeIcon, "/com/shelton/ebu6403/images/hide.png");

    }

    /**
     * Loads an image from resources into an ImageView.
     * @param imageView The ImageView to load the image into
     * @param resourcePath The path to the image resource
     */
    private void loadImage(ImageView imageView, String resourcePath) {
        try {
            Image image = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream(resourcePath)
            ));
            imageView.setImage(image);
        } catch (NullPointerException e) {
            System.err.println("Failed to load resource: " + resourcePath);
            // Set default placeholder or handle error
        }
    }

    /**
     * Handles the login button click event.
     * Validates credentials and opens the main application if successful.
     * @throws IOException If there's an error loading the main view
     */
    @FXML
    private void handleLogin() throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordVisible ? passwordVisibleField.getText().trim()
                : passwordField.getText().trim();

        boolean loginSuccess = checkCredentials(username, password);

        if (!loginSuccess) {
            loginMessage.setText("Login failed. Hint: It's your school");
            return;
        }

        loginMessage.setText(""); // Clear message
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

    /**
     * Validates user credentials against stored values.
     * @param user The username to check
     * @param pass The password to check
     * @return true if credentials are valid, false otherwise
     */
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

        // If no match found in file, use default credentials
        return user.equals("BUPT") && pass.equals("BUPT");
    }

    /**
     * Toggles password visibility between hidden and visible states.
     * Updates the UI to show/hide password and changes the eye icon accordingly.
     */
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

    /**
     * Shows a dialog for creating a new user account.
     * Validates input fields and saves the new account if validation passes.
     */
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

    /**
     * Saves new user credentials to the users database file.
     * Appends the username and password to the CSV file.
     * @param username The username to save
     * @param password The password to save
     */
    private void saveUserToFile(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/users.csv", true))) {
            writer.newLine();
            writer.write(username + "," + password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays a warning alert dialog.
     * Shows validation errors or other important messages to the user.
     * @param msg The message to display in the alert
     */
    private void showInlineAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Checks if a username is already registered.
     * Performs a case-insensitive search in the users database file.
     * @param username The username to check for existence
     * @return true if the username exists, false otherwise
     */
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
}

