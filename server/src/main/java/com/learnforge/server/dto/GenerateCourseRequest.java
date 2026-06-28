package com.learnforge.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GenerateCourseRequest {
    @NotBlank(message = "topic is required")
    @Size(min = 3, max = 180, message = "topic should be between 3 and 180 characters")
    private String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
