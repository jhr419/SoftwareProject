package com.shelton.ebu6403.controller;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.ComboBox;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import java.util.Objects;

public class LedgerController {
    public BorderPane contentContainer;
    @FXML private BorderPane mainContainer;
    @FXML private ImageView notificationIcon;
    @FXML private ImageView avatarView;
    @FXML private ComboBox<String> languageCombo;

    @FXML
    public void initialize() {
        loadDynamicImages();
        // 初始化语言选择框
        initLanguageSelector();
        // 动态加载 CSS
        String cssPath = Objects.requireNonNull(getClass().getResource(
                "/com/shelton/ebu6403/styles/main.css"
        )).toExternalForm();
        mainContainer.getStylesheets().add(cssPath);
    }

    private void initLanguageSelector() {
        // 设置默认选中英语
        languageCombo.getSelectionModel().select("English");

        // 添加选择监听器
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

    private void switchToChinese() {
        // 这里添加切换到中文的逻辑
        System.out.println("切换到中文界面");
    }

    private void switchToEnglish() {
        // 这里添加切换到英文的逻辑
        System.out.println("切换到英文界面");
    }

    private void loadDynamicImages() {
        loadImage(notificationIcon, "/com/shelton/ebu6403/images/notification.png");
        loadImage(avatarView, "/com/shelton/ebu6403/images/avatar.png");
    }

    private void loadImage(ImageView imageView, String path) {
        try {
            Image image = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream(path)
            ));
            imageView.setImage(image);
        } catch (NullPointerException e) {
            System.err.println("资源加载失败: " + path);
            imageView.setImage(createPlaceholder(imageView.getFitWidth(), imageView.getFitHeight()));
        }
    }

    private Image createPlaceholder(double width, double height) {
        // 生成灰色占位图
        return new Image(
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAAXNSR0IArs4c6QAAAA1JREFUGFdjYGBgYAAAAAUAAYehTtQAAAAASUVORK5CYII=",
                width, height, true, true
        );
    }

    // 原有页面导航方法保持不变
    @FXML
    private void loadHome() { loadView("/com/shelton/ebu6403/views/home.fxml"); }

    @FXML
    private void loadAnalysis() { loadView("/com/shelton/ebu6403/views/analysis.fxml"); }

    @FXML
    private void loadCategories() { loadView("/com/shelton/ebu6403/views/categories.fxml"); }

    @FXML
    private void loadInvestments() { loadView("/com/shelton/ebu6403/views/investments.fxml"); }

    @FXML
    private void loadSettings() { loadView("/com/shelton/ebu6403/views/settings.fxml"); }

    // 修改页面加载方法，只替换中心区域
// 页面加载方法只需修改这一部分：
    private void loadView(String fxmlPath) {
        try {
            // 获取右侧的嵌套BorderPane
            BorderPane rightPane = (BorderPane) mainContainer.getCenter();

            // 只替换中心内容区域（保留顶部工具栏）
            Parent view = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource(fxmlPath))
            );
            rightPane.setCenter(view);

        } catch (Exception e) {
            System.err.println("视图加载失败: " + fxmlPath);
            e.printStackTrace();
        }
    }
}


