import java.io.*;
import java.net.*;
import org.json.JSONObject;
import javax.swing.JTextArea;

public class ApiQA {
    private static final String URL = "https://api.siliconflow.cn/v1/chat/completions";
    private String apiKey;

    public ApiQA(String apiKey) {
        this.apiKey = apiKey;
    }

    public void sendRequest(String question, JTextArea responseArea) throws Exception {
        // 构建请求的 JSON 数据
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("model", "Qwen/Qwen2.5-72B-Instruct");
        jsonPayload.put("stream", false);  // 保持流式模式
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

        // 读取响应并逐字输出
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // 解析响应并提取 content 部分
            JSONObject responseJson = new JSONObject(response.toString());
            String answer = responseJson.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            // 实时逐字输出（带模型名称前缀）
            responseArea.append("Qwen: ");  // 模型名称
            for (int i = 0; i < answer.length(); i++) {
                String character = String.valueOf(answer.charAt(i));
                responseArea.append(character);  // 输出每个字符
                responseArea.setCaretPosition(responseArea.getDocument().getLength());  // 滚动到最后
                Thread.sleep(50);  // 控制每个字符的输出速度，可以调整这个值
            }
            responseArea.append("\n\n");  // 添加换行
        }
    }
}
