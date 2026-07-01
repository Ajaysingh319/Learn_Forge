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
import java.util.Base64;
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
                String detail = response.body();
                if (detail != null && detail.length() > 240) {
                    detail = detail.substring(0, 240) + "...";
                }
                throw new AiGenerationException(
                        "Gemini request failed with status " + response.statusCode()
                                + (detail == null || detail.isBlank() ? "" : ": " + detail)
                );
            }
            return extractText(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new AiGenerationException("Gemini request interrupted", ex);
        } catch (IOException ex) {
            throw new AiGenerationException("Gemini request failed", ex);
        }
    }

    /**
     * Calls the Gemini TTS model and returns raw PCM audio plus its sample rate.
     */
    public AudioData generateSpeech(String text, String voiceName) {
        String apiKey = geminiProperties.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new AiGenerationException("GEMINI_API_KEY is missing");
        }
        String voice = voiceName == null || voiceName.trim().isEmpty()
                ? geminiProperties.getTtsVoiceName()
                : voiceName.trim();

        try {
            Map<String, Object> textPart = new HashMap<String, Object>();
            textPart.put("text", text);
            Map<String, Object> content = new HashMap<String, Object>();
            content.put("parts", Arrays.asList(textPart));

            Map<String, Object> prebuilt = new HashMap<String, Object>();
            prebuilt.put("voiceName", voice);
            Map<String, Object> voiceConfig = new HashMap<String, Object>();
            voiceConfig.put("prebuiltVoiceConfig", prebuilt);
            Map<String, Object> speechConfig = new HashMap<String, Object>();
            speechConfig.put("voiceConfig", voiceConfig);
            Map<String, Object> generationConfig = new HashMap<String, Object>();
            generationConfig.put("responseModalities", Arrays.asList("AUDIO"));
            generationConfig.put("speechConfig", speechConfig);

            Map<String, Object> payload = new HashMap<String, Object>();
            payload.put("contents", Arrays.asList(content));
            payload.put("generationConfig", generationConfig);

            String endpoint = geminiProperties.getBaseUrl()
                    + "/"
                    + geminiProperties.getTtsModel()
                    + ":generateContent?key="
                    + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = HttpRetryHelper.sendWithRetry(
                    httpClient,
                    request,
                    aiProperties.getMaxRetries() + 1
            );
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String detail = response.body();
                if (detail != null && detail.length() > 240) {
                    detail = detail.substring(0, 240) + "...";
                }
                throw new AiGenerationException(
                        "Gemini TTS request failed with status " + response.statusCode()
                                + (detail == null || detail.isBlank() ? "" : ": " + detail)
                );
            }
            return extractAudio(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new AiGenerationException("Gemini TTS request interrupted", ex);
        } catch (IOException ex) {
            throw new AiGenerationException("Gemini TTS request failed", ex);
        }
    }

    private AudioData extractAudio(String rawBody) throws IOException {
        Map<String, Object> root = objectMapper.readValue(rawBody, MAP_TYPE);
        Object candidatesObj = root.get("candidates");
        if (!(candidatesObj instanceof List<?>) || ((List<?>) candidatesObj).isEmpty()) {
            throw new AiGenerationException("Gemini TTS response has no candidates");
        }
        Object firstCandidate = ((List<?>) candidatesObj).get(0);
        if (!(firstCandidate instanceof Map<?, ?>)) {
            throw new AiGenerationException("Gemini TTS response has invalid candidate format");
        }
        Object contentObj = ((Map<?, ?>) firstCandidate).get("content");
        if (!(contentObj instanceof Map<?, ?>)) {
            throw new AiGenerationException("Gemini TTS response has invalid content format");
        }
        Object partsObj = ((Map<?, ?>) contentObj).get("parts");
        if (!(partsObj instanceof List<?>) || ((List<?>) partsObj).isEmpty()) {
            throw new AiGenerationException("Gemini TTS response has no audio parts");
        }
        Object firstPart = ((List<?>) partsObj).get(0);
        if (!(firstPart instanceof Map<?, ?>)) {
            throw new AiGenerationException("Gemini TTS response has invalid part format");
        }
        Object inlineDataObj = ((Map<?, ?>) firstPart).get("inlineData");
        if (!(inlineDataObj instanceof Map<?, ?>)) {
            throw new AiGenerationException("Gemini TTS response has no inline audio data");
        }
        Map<?, ?> inlineData = (Map<?, ?>) inlineDataObj;
        Object dataObj = inlineData.get("data");
        if (!(dataObj instanceof String) || ((String) dataObj).isBlank()) {
            throw new AiGenerationException("Gemini TTS response has empty audio data");
        }
        byte[] pcm = Base64.getDecoder().decode(((String) dataObj).trim());
        int sampleRate = parseSampleRate(inlineData.get("mimeType"));
        return new AudioData(pcm, sampleRate);
    }

    private int parseSampleRate(Object mimeTypeObj) {
        int defaultRate = 24000;
        if (!(mimeTypeObj instanceof String)) {
            return defaultRate;
        }
        String mimeType = (String) mimeTypeObj;
        for (String segment : mimeType.split(";")) {
            String trimmed = segment.trim();
            if (trimmed.startsWith("rate=")) {
                try {
                    return Integer.parseInt(trimmed.substring("rate=".length()).trim());
                } catch (NumberFormatException ex) {
                    return defaultRate;
                }
            }
        }
        return defaultRate;
    }

    public static final class AudioData {
        private final byte[] pcm;
        private final int sampleRate;

        public AudioData(byte[] pcm, int sampleRate) {
            this.pcm = pcm;
            this.sampleRate = sampleRate;
        }

        public byte[] getPcm() {
            return pcm;
        }

        public int getSampleRate() {
            return sampleRate;
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
