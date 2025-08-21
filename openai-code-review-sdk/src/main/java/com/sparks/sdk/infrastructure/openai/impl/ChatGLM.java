package com.sparks.sdk.infrastructure.openai.impl;


import com.alibaba.fastjson2.JSON;
import com.sparks.sdk.infrastructure.openai.dto.ChatCompletionRequestDTO;
import com.sparks.sdk.infrastructure.openai.dto.ChatCompletionSyncResponseDTO;
import com.sparks.sdk.types.utils.BearerTokenUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ChatGLM implements IOpenAI {
    private final String apiKeySecret;
    private final String apiHost;

    public ChatGLM(String apiHost, String apiKeySecret) {
        this.apiKeySecret = apiKeySecret;
        this.apiHost = apiHost;
    }

    @Override
    public ChatCompletionSyncResponseDTO completions(ChatCompletionRequestDTO chatRequestDTO) throws Exception {
        String token = BearerTokenUtils.getToken(apiKeySecret);

        URL url = new URL(apiHost);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = JSON.toJSONString(chatRequestDTO).getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();

        return JSON.parseObject(content.toString(), ChatCompletionSyncResponseDTO.class);
    }
}