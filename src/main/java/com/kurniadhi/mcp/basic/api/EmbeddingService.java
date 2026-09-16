package com.kurniadhi.mcp.basic.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Service
public class EmbeddingService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public EmbeddingService(
            @Value("${app.openai.api-key:}") String apiKey,
            ObjectMapper objectMapper
    ) {
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public byte[] embedToBytes(String text) {
        float[] vector = embedToFloats(text);
        ByteBuffer buf = ByteBuffer.allocate(vector.length * 4);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        for (float v : vector) {
            buf.putFloat(v);
        }
        return buf.array();
    }

    public float[] embedToFloats(String text) {
        if (apiKey.isEmpty()) {
            throw new IllegalStateException("Set app.openai.api-key to enable embeddings");
        }
        try {
            String requestBody = objectMapper.writeValueAsString(
                    new EmbeddingRequest("text-embedding-ada-002", text)
            );
            String responseBody = restClient.post()
                    .uri("/v1/embeddings")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode embeddingArray = root.get("data").get(0).get("embedding");
            float[] result = new float[embeddingArray.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = (float) embeddingArray.get(i).asDouble();
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate embedding", e);
        }
    }

    public int dimensions() {
        return 384;
    }

    private record EmbeddingRequest(String model, String input) {}
}
