package com.shelton.ebu6403;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LedgerApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ledger.fxml"));
        BorderPane root = loader.load();

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Ledger Application");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/shelton/ebu6403/icon.png")));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

