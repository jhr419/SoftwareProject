package main.java.com.shelton.ebu6403;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.InputStream;

public class LedgerApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/shelton/ebu6403/views/Login.fxml")
        );
        Parent root = loader.load();

        // 动态加载窗口图标
        try (InputStream iconStream = getClass().getResourceAsStream(
                "/com/shelton/ebu6403/images/icon.png")) {
            primaryStage.getIcons().add(new Image(iconStream));
        }

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}