package com.learnforge.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gemini")
public class GeminiProperties {
    private String apiKey = "";
    private String textModel = "gemini-2.5-flash";
    private String ttsVoiceName = "Kore";
    private String baseUrl = "https://generativelanguage.googleapis.com/v1beta/models";

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getTextModel() {
        return textModel;
    }

    public void setTextModel(String textModel) {
        this.textModel = textModel;
    }

    public String getTtsVoiceName() {
        return ttsVoiceName;
    }

    public void setTtsVoiceName(String ttsVoiceName) {
        this.ttsVoiceName = ttsVoiceName;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
