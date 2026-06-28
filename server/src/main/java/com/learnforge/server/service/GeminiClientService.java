package com.learnforge.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnforge.server.config.AiProperties;
import com.learnforge.server.config.GeminiProperties;
import com.learnforge.server.exception.AiGenerationException;
import com.learnforge.server.util.HttpRetryHelper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class GeminiClientService {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<Map<String, Object>>() {
    };

    private final GeminiProperties geminiProperties;
    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public GeminiClientService(
            GeminiProperties geminiProperties,
            AiProperties aiProperties,
            ObjectMapper objectMapper
    ) {
        this.geminiProperties = geminiProperties;
        this.aiProperties = aiProperties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String generateText(String prompt) {
        String apiKey = geminiProperties.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new AiGenerationException("GEMINI_API_KEY is missing");
        }

        try {
            Map<String, Object> textPart = new HashMap<String, Object>();
            textPart.put("text", prompt);

            Map<String, Object> content = new HashMap<String, Object>();
            content.put("parts", Arrays.asList(textPart));

            Map<String, Object> payload = new HashMap<String, Object>();
            payload.put("contents", Arrays.asList(content));

            String endpoint = geminiProperties.getBaseUrl()
                    + "/"
                    + geminiProperties.getTextModel()
                    + ":generateContent?key="
                    + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = HttpRetryHelper.sendWithRetry(
                    httpClient,
                    request,
                    aiProperties.getMaxRetries() + 1
            );
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new AiGenerationException("Gemini request failed with status " + response.statusCode());
            }
            return extractText(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new AiGenerationException("Gemini request interrupted", ex);
        } catch (IOException ex) {
            throw new AiGenerationException("Gemini request failed", ex);
        }
    }

    private String extractText(String rawBody) throws IOException {
        Map<String, Object> root = objectMapper.readValue(rawBody, MAP_TYPE);
        Object candidatesObj = root.get("candidates");
        if (!(candidatesObj instanceof List<?>)) {
            throw new AiGenerationException("Gemini response has no candidates");
        }
        List<?> candidates = (List<?>) candidatesObj;
        if (candidates.isEmpty() || !(candidates.get(0) instanceof Map<?, ?>)) {
            throw new AiGenerationException("Gemini response has invalid candidate format");
        }
        Map<?, ?> firstCandidate = (Map<?, ?>) candidates.get(0);
        Object contentObj = firstCandidate.get("content");
        if (!(contentObj instanceof Map<?, ?>)) {
            throw new AiGenerationException("Gemini response has invalid content format");
        }
        Map<?, ?> content = (Map<?, ?>) contentObj;
        Object partsObj = content.get("parts");
        if (!(partsObj instanceof List<?>)) {
            throw new AiGenerationException("Gemini response has no parts");
        }
        List<?> parts = (List<?>) partsObj;
        if (parts.isEmpty() || !(parts.get(0) instanceof Map<?, ?>)) {
            throw new AiGenerationException("Gemini response has invalid parts format");
        }
        Map<?, ?> firstPart = (Map<?, ?>) parts.get(0);
        Object textObj = firstPart.get("text");
        if (!(textObj instanceof String)) {
            throw new AiGenerationException("Gemini response has no text");
        }
        String text = (String) textObj;
        if (text.trim().isEmpty()) {
            throw new AiGenerationException("Gemini response text is empty");
        }
        return text.trim();
    }
}
