// AiDeepseekController.java
package com.shelton.ebu6403.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class AiDeepseekController {
    @FXML private VBox aiContainer;
    @FXML private TextArea inputArea;
    @FXML private TextArea outputArea;
    @FXML private Button sendButton;

    private final String apiKey = "sk-cbzpgeqjquxjgusngdklsmrzikmptukukbrvzbjhibsosfyf"; // 替换成你的 API Key

    @FXML
    public void initialize() {
        sendButton.setOnAction(e -> {
            String question = inputArea.getText().trim();
            if (!question.isEmpty()) {
                outputArea.appendText("You: " + question + "\n");
                inputArea.clear();
                new Thread(() -> {
                    try {
                        String answer = sendRequest(question);
                        outputArea.appendText("AI: " + answer + "\n\n");
                    } catch (Exception ex) {
                        outputArea.appendText("Error: " + ex.getMessage() + "\n\n");
                    }
                }).start();
            }
        });
    }

    private String sendRequest(String question) throws Exception {
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("model", "Qwen/Qwen2.5-72B-Instruct");
        jsonPayload.put("stream", false);
        jsonPayload.put("max_tokens", 2048);
        jsonPayload.put("temperature", 0.7);
        jsonPayload.put("top_p", 0.7);
        jsonPayload.put("top_k", 50);
        jsonPayload.put("frequency_penalty", 0.5);
        jsonPayload.put("n", 1);

        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", question);
        messages.put(message);
        jsonPayload.put("messages", messages);

        URL url = new URL("https://api.siliconflow.cn/v1/chat/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            JSONObject responseJson = new JSONObject(response.toString());
            return responseJson.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        }
    }
}
