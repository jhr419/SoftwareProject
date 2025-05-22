package main.java.com.shelton.ebu6403.models;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {
    private static final String URL = "https://api.siliconflow.cn/v1/chat/completions";
    private String apiKey;

    public ApiClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public String sendRequest(String question) throws Exception {
        // 构建请求的 JSON 数据
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("model", "Qwen/Qwen2.5-72B-Instruct");
        jsonPayload.put("stream", false);
        jsonPayload.put("max_tokens", 2048);
        jsonPayload.put("temperature", 0.7);
        jsonPayload.put("top_p", 0.7);
        jsonPayload.put("top_k", 50);
        jsonPayload.put("frequency_penalty", 0.5);
        jsonPayload.put("n", 1);

        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", question);

        jsonPayload.put("messages", new JSONObject[] { message });

        URL url = new URL(URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // 读取响应
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            // 解析响应并仅提取 content 部分
            JSONObject responseJson = new JSONObject(response.toString());
            String answer = responseJson.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            //System.out.println(answer);
            // 返回 content 作为回答
            return answer;
        }
    }
}
