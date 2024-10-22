package com.globex.service;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class CohereService {

    private static final String API_URL = "https://api.cohere.ai/v1/generate";
    private final String apiKey = "ZAS18EHsskcpW1F0DxumhGVfjCea4f1WN6Adr7kB";
    private static final Logger logger = LoggerFactory.getLogger(CohereService.class);

	/*
	 * public CohereService() { this.apiKey = System.getenv("COHERE_API_KEY"); }
	 */

    public String generateText(String prompt, int maxTokens, double temperature) throws Exception {
        OkHttpClient client = new OkHttpClient();

        String json = "{"
            + "\"prompt\":\"" + prompt + "\","
            + "\"max_tokens\":" + maxTokens + ","
            + "\"temperature\":" + temperature
            + "}";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
            .url(API_URL)
            .header("Authorization", "Bearer " + apiKey)
            .post(body)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.error("Cohere API request failed: {} - {}", response.code(), response.message());
                throw new IOException("Cohere API request failed with code: " + response.code());
            }

            String responseBody = response.body().string();
            logger.info("Cohere Response: {}", responseBody);
            return responseBody;
        }
    }

}


