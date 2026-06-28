package com.learnforge.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnforge.server.config.AiProperties;
import com.learnforge.server.exception.AiGenerationException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class OpenAiCourseOutlineProvider implements CourseOutlineAiProvider {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<Map<String, Object>>() {
    };
    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public OpenAiCourseOutlineProvider(AiProperties aiProperties, ObjectMapper objectMapper) {
        this.aiProperties = aiProperties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public String generateJsonOutline(String prompt) {
        String apiKey = aiProperties.getOpenai().getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new AiGenerationException("OPENAI_API_KEY is missing for openai provider");
        }

        try {
            String payload = objectMapper.writeValueAsString(Map.of(
                    "model", aiProperties.getOpenai().getModel(),
                    "temperature", aiProperties.getOpenai().getTemperature(),
                    "messages", List.of(
                            Map.of("role", "system", "content", "You generate strict JSON responses."),
                            Map.of("role", "user", "content", prompt)
                    )
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(aiProperties.getOpenai().getBaseUrl()))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new AiGenerationException("OpenAI request failed with status " + response.statusCode());
            }
            return extractMessageContent(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new AiGenerationException("OpenAI request failed", ex);
        } catch (IOException ex) {
            throw new AiGenerationException("OpenAI request failed", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private String extractMessageContent(String body) throws IOException {
        Map<String, Object> parsed = objectMapper.readValue(body, MAP_TYPE);
        Object choicesObj = parsed.get("choices");
        if (!(choicesObj instanceof List<?>)) {
            throw new AiGenerationException("OpenAI response has no choices");
        }
        List<?> choices = (List<?>) choicesObj;
        if (choices.isEmpty()) {
            throw new AiGenerationException("OpenAI response has no choices");
        }
        Object firstChoice = choices.get(0);
        if (!(firstChoice instanceof Map<?, ?>)) {
            throw new AiGenerationException("OpenAI response has invalid choice format");
        }
        Map<?, ?> choiceMap = (Map<?, ?>) firstChoice;
        Object messageObj = choiceMap.get("message");
        if (!(messageObj instanceof Map<?, ?>)) {
            throw new AiGenerationException("OpenAI response has invalid message format");
        }
        Map<?, ?> messageMap = (Map<?, ?>) messageObj;
        Object contentObj = messageMap.get("content");
        if (!(contentObj instanceof String)) {
            throw new AiGenerationException("OpenAI response has empty content");
        }
        String content = (String) contentObj;
        if (content.trim().isEmpty()) {
            throw new AiGenerationException("OpenAI response has empty content");
        }
        return content;
    }
}
