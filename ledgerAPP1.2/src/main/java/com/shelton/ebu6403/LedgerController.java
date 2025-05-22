package com.shelton.ebu6403;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class LedgerController {
    @FXML private ImageView userAvatar;
    @FXML private Label userName;
    @FXML private StackPane contentPane;
    @FXML private Button btnStatistics;
    @FXML private Button btnDetails;
    @FXML private Button btnFinance;

    public void initialize() {
        // 设置默认头像
        userAvatar.setImage(new Image(getClass().getResourceAsStream("/com/shelton/ebu6403/avatar.png")));
        userName.setText("欢迎, 用户");

        // 默认加载统计页面
        switchToStatistics();
    }

    private void loadPage(String page) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(page));
            Parent newPage = loader.load(); // 加载 FXML 页面
            contentPane.getChildren().setAll(newPage); // 只传一个 Parent 元素
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToStatistics() {
        loadPage("statistics.fxml");
    }

    @FXML
    private void switchToDetails() {
        loadPage("details.fxml");
    }

    @FXML
    private void switchToFinance() {
        loadPage("finance.fxml");
    }
}
