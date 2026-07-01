package com.learnforge.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TtsRequest {
    @NotBlank(message = "text is required")
    @Size(max = 8000, message = "text must be at most 8000 characters")
    private String text;
    private String voiceName;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getVoiceName() {
        return voiceName;
    }

    public void setVoiceName(String voiceName) {
        this.voiceName = voiceName;
    }
}
