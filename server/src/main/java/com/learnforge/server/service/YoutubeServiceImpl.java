package com.learnforge.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnforge.server.config.YoutubeProperties;
import com.learnforge.server.dto.YoutubeVideoResponse;
import com.learnforge.server.exception.AiGenerationException;
import com.learnforge.server.exception.BadRequestException;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class YoutubeServiceImpl implements YoutubeService {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<Map<String, Object>>() {
    };
    private final YoutubeProperties youtubeProperties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final ConcurrentHashMap<String, CachedSearchResult> cache = new ConcurrentHashMap<String, CachedSearchResult>();

    public YoutubeServiceImpl(YoutubeProperties youtubeProperties, ObjectMapper objectMapper) {
        this.youtubeProperties = youtubeProperties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .build();
    }

    @Override
    public List<YoutubeVideoResponse> searchVideos(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new BadRequestException("query parameter is required");
        }

        String normalizedQuery = query.trim();
        CachedSearchResult cached = cache.get(normalizedQuery.toLowerCase());
        if (cached != null && cached.expiresAt.isAfter(Instant.now())) {
            return cached.results;
        }

        String apiKey = youtubeProperties.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new AiGenerationException("YOUTUBE_API_KEY is missing");
        }

        String endpoint = buildSearchUrl(normalizedQuery, apiKey);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new AiGenerationException("YouTube API request failed with status " + response.statusCode());
            }
            List<YoutubeVideoResponse> results = parseResults(response.body());
            cache.put(normalizedQuery.toLowerCase(), new CachedSearchResult(
                    results,
                    Instant.now().plusSeconds(youtubeProperties.getCacheTtlSeconds())
            ));
            return results;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new AiGenerationException("YouTube API request interrupted", ex);
        } catch (IOException ex) {
            throw new AiGenerationException("YouTube API request failed", ex);
        }
    }

    private String buildSearchUrl(String query, String apiKey) {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        return youtubeProperties.getBaseUrl()
                + "?part=snippet"
                + "&type=video"
                + "&videoEmbeddable=true"
                + "&maxResults=" + youtubeProperties.getMaxResults()
                + "&q=" + encodedQuery
                + "&key=" + apiKey;
    }

    @SuppressWarnings("unchecked")
    private List<YoutubeVideoResponse> parseResults(String rawBody) throws IOException {
        Map<String, Object> root = objectMapper.readValue(rawBody, MAP_TYPE);
        Object itemsObj = root.get("items");
        if (!(itemsObj instanceof List<?>)) {
            return Collections.emptyList();
        }
        List<?> items = (List<?>) itemsObj;

        List<YoutubeVideoResponse> results = new ArrayList<YoutubeVideoResponse>();
        for (Object itemObj : items) {
            if (!(itemObj instanceof Map<?, ?>)) {
                continue;
            }
            Map<?, ?> itemMap = (Map<?, ?>) itemObj;
            Object idObj = itemMap.get("id");
            Object snippetObj = itemMap.get("snippet");
            if (!(idObj instanceof Map<?, ?>) || !(snippetObj instanceof Map<?, ?>)) {
                continue;
            }
            Map<?, ?> idMap = (Map<?, ?>) idObj;
            Map<?, ?> snippetMap = (Map<?, ?>) snippetObj;
            Object videoIdObj = idMap.get("videoId");
            if (!(videoIdObj instanceof String)) {
                continue;
            }
            String videoId = (String) videoIdObj;
            if (videoId.trim().isEmpty()) {
                continue;
            }

            YoutubeVideoResponse video = new YoutubeVideoResponse();
            video.setVideoId(videoId);
            video.setEmbedUrl("https://www.youtube.com/embed/" + videoId);
            video.setTitle(stringValue(snippetMap.get("title")));
            video.setChannelTitle(stringValue(snippetMap.get("channelTitle")));
            video.setThumbnailUrl(extractThumbnail(snippetMap.get("thumbnails")));
            results.add(video);
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private String extractThumbnail(Object thumbnailsObj) {
        if (!(thumbnailsObj instanceof Map<?, ?>)) {
            return "";
        }
        Map<?, ?> thumbnails = (Map<?, ?>) thumbnailsObj;
        String[] priorities = {"high", "medium", "default"};
        for (String priority : priorities) {
            Object candidateObj = thumbnails.get(priority);
            if (candidateObj instanceof Map<?, ?>) {
                Map<?, ?> candidate = (Map<?, ?>) candidateObj;
                Object urlObj = candidate.get("url");
                if (urlObj instanceof String) {
                    String url = (String) urlObj;
                    if (!url.trim().isEmpty()) {
                    return url;
                    }
                }
            }
        }
        return "";
    }

    private String stringValue(Object value) {
        if (value instanceof String) {
            return (String) value;
        }
        return "";
    }

    private static class CachedSearchResult {
        private final List<YoutubeVideoResponse> results;
        private final Instant expiresAt;

        private CachedSearchResult(List<YoutubeVideoResponse> results, Instant expiresAt) {
            this.results = results;
            this.expiresAt = expiresAt;
        }
    }
}
