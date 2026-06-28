package com.learnforge.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TranslateRequest {
    @NotBlank(message = "text is required")
    @Size(max = 8000, message = "text must be at most 8000 characters")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
