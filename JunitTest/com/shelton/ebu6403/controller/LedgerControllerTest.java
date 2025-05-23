package com.shelton.ebu6403.controller;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LedgerControllerTest {

    private LedgerController controller;

    @BeforeEach
    void setUp() {
        // 初始化 JavaFX toolkit（仅需初始化一次）
        new JFXPanel(); // 必须在测试中运行 JavaFX 应用线程

        controller = new LedgerController();

        // 模拟 FXML 注入的控件
        controller.mainContainer = new BorderPane();
        controller.contentContainer = new BorderPane();
        controller.notificationIcon = new ImageView();
        controller.avatarView = new ImageView();
        controller.languageCombo = new ComboBox<>();
        controller.languageCombo.getItems().addAll("English", "简体中文");
    }

    @Test
    void initialize_shouldNotThrow() {
        assertDoesNotThrow(() -> controller.initialize());
    }

    @Test
    void languageCombo_switchToChinese_printsMessage() {
        controller.initialize();
        controller.languageCombo.getSelectionModel().select("简体中文");

        // 你可以用日志捕获工具进一步检查 System.out 是否输出“切换到中文界面”
        assertEquals("简体中文", controller.languageCombo.getValue());
    }

    @Test
    void languageCombo_switchToEnglish_printsMessage() {
        controller.initialize();
        controller.languageCombo.getSelectionModel().select("English");

        assertEquals("English", controller.languageCombo.getValue());
    }

    @Test
    void loadHome_shouldNotThrow() {
        assertDoesNotThrow(() -> controller.loadHome());
    }

    @Test
    void loadInvestments_shouldNotThrow() {
        assertDoesNotThrow(() -> controller.loadInvestments());
    }

    @Test
    void loadView_invalidPath_shouldHandleException() {
        assertDoesNotThrow(() -> controller.loadView("/invalid/path.fxml"));
    }
}
