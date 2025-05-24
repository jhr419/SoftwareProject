package com.shelton.ebu6403;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;

/**
 * Main application class for LedgerEase.
 * <p>
 * This class initializes and launches the JavaFX UI by loading the Login.fxml layout.
 * It also sets the main application icon and displays the initial window.
 * </p>
 *
 * author Jia Liu, Haihan Sun, Weicheng Xie
 */
public class LedgerApp extends Application {

    /**
     * Starts the JavaFX application.
     * <p>
     * Loads the login screen from FXML, sets the scene and application icon, and shows the primary stage.
     * </p>
     *
     * @param primaryStage the main stage provided by JavaFX
     * @throws Exception if FXML or resources cannot be loaded
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/shelton/ebu6403/views/Login.fxml")
        );
        Parent root = loader.load();

        // Load and set application icon
        try (InputStream iconStream = getClass().getResourceAsStream(
                "/com/shelton/ebu6403/images/icon.png")) {
            primaryStage.getIcons().add(new Image(iconStream));
        }

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("LedgerEase");
        primaryStage.show();
    }

    /**
     * Launches the application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
