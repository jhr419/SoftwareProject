package com.shelton.ebu6403.models;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A client class for sending HTTP requests to an AI API endpoint.
 * <p>
 * This class constructs JSON requests using the specified API key and sends
 * prompts to the Qwen model, then parses and returns the AI-generated response.
 * </p>
 *
 * @author Zhifei Liu, Weicheng Xie, Jia Liu
 */
public class ApiClient {

    /** The fixed URL endpoint of the API. */
    private static final String URL = "https://api.siliconflow.cn/v1/chat/completions";

    /** The API key used for authorization. */
    private String apiKey;

    /**
     * Constructs an ApiClient with the provided API key.
     *
     * @param apiKey the access token for the API
     */
    public ApiClient(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Sends a prompt to the AI model and returns the generated response.
     * <p>
     * This method constructs the JSON payload, initiates an HTTP POST request,
     * and extracts the content field from the returned JSON response.
     * </p>
     *
     * @param question the user input or prompt to be sent to the AI
     * @return the AI model's response content
     * @throws Exception if a network or parsing error occurs
     */
    public String sendRequest(String question) throws Exception {
        // Construct the JSON payload
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

        // Send the HTTP POST request
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

        // Read and parse the response content
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
