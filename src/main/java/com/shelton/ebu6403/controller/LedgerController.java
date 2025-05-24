package com.shelton.ebu6403.controller;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.ComboBox;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import java.util.Objects;

/**
 * Controller class for the main ledger interface.
 * <p>
 * Manages the main application layout, navigation, language switching,
 * and dynamic resource loading.
 * </p>
 * author Haihan Sun, Jia LIU, Zuhao Zhang
 */
public class LedgerController {
    /** Content container for dynamic views */
    public BorderPane contentContainer;
    /** Main application container */
    @FXML private BorderPane mainContainer;
    /** Notification icon in the header */
    @FXML private ImageView notificationIcon;
    /** User avatar image */
    @FXML private ImageView avatarView;
    /** Language selection dropdown */
    @FXML private ComboBox<String> languageCombo;

    /**
     * Initializes the controller.
     * Loads images, sets up language selector, and applies CSS styles.
     */
    @FXML
    public void initialize() {
        loadDynamicImages();
        initLanguageSelector();

        String cssPath = Objects.requireNonNull(getClass().getResource(
                "/com/shelton/ebu6403/styles/main.css"
        )).toExternalForm();
        mainContainer.getStylesheets().add(cssPath);
    }

    /**
     * Initializes the language selector dropdown.
     * Sets default language and adds change listener.
     */
    private void initLanguageSelector() {
        languageCombo.getSelectionModel().select("English");

        languageCombo.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if ("简体中文".equals(newVal)) {
                        switchToChinese();
                    } else {
                        switchToEnglish();
                    }
                }
        );
    }

    /**
     * Switches the application interface to Chinese.
     */
    private void switchToChinese() {
        System.out.println("Switching to Chinese interface");
    }

    /**
     * Switches the application interface to English.
     */
    private void switchToEnglish() {
        System.out.println("Switching to English interface");
    }

    /**
     * Loads all dynamic images used in the interface.
     */
    private void loadDynamicImages() {
        loadImage(notificationIcon, "/com/shelton/ebu6403/images/notification.png");
        loadImage(avatarView, "/com/shelton/ebu6403/images/profile photo.png");
    }

    /**
     * Loads an image into an ImageView component.
     * @param imageView The ImageView to load into
     * @param path The resource path of the image
     */
    private void loadImage(ImageView imageView, String path) {
        try {
            Image image = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream(path)
            ));
            imageView.setImage(image);
        } catch (NullPointerException e) {
            System.err.println("Failed to load resource: " + path);
            imageView.setImage(createPlaceholder(imageView.getFitWidth(), imageView.getFitHeight()));
        }
    }

    /**
     * Creates a placeholder image when resource loading fails.
     * @param width The width of the placeholder
     * @param height The height of the placeholder
     * @return A gray placeholder Image
     */
    private Image createPlaceholder(double width, double height) {
        return new Image(
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAAXNSR0IArs4c6QAAAA1JREFUGFdjYGBgYAAAAAUAAYehTtQAAAAASUVORK5CYII=",
                width, height, true, true
        );
    }

    /**
     * Loads the home view.
     */
    @FXML
    private void loadHome() {
        loadView("/com/shelton/ebu6403/views/home.fxml");
    }

    /**
     * Loads the analysis view.
     */
    @FXML
    private void loadAnalysis() {
        loadView("/com/shelton/ebu6403/views/analysis.fxml");
    }

    /**
     * Loads the categories view.
     */
    @FXML
    private void loadCategories() {
        loadView("/com/shelton/ebu6403/views/categories.fxml");
    }

    /**
     * Loads the investments view.
     */
    @FXML
    private void loadInvestments() {
        loadView("/com/shelton/ebu6403/views/investments.fxml");
    }

    /**
     * Loads the settings view.
     */
    @FXML
    private void loadSettings() {
        loadView("/com/shelton/ebu6403/views/Setting.fxml");
    }

    /**
     * Loads the AI chat interface view.
     */
    @FXML
    private void loadAiChat() {
        loadView("/com/shelton/ebu6403/views/AiDeepseekView.fxml");
    }

    /**
     * Loads a view into the main content area.
     * Only replaces the center region of the right BorderPane.
     * @param fxmlPath The FXML resource path of the view to load
     */
    private void loadView(String fxmlPath) {
        try {
            BorderPane rightPane = (BorderPane) mainContainer.getCenter();
            Parent view = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource(fxmlPath))
            );
            rightPane.setCenter(view);
        } catch (Exception e) {
            System.err.println("Failed to load view: " + fxmlPath);
            e.printStackTrace();
        }
    }
}

