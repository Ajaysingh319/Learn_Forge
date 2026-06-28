package com.learnforge.server.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeneratedLessonResponse {
    @NotBlank(message = "title is required")
    private String title;

    @NotEmpty(message = "objectives are required")
    private List<@NotBlank(message = "objective cannot be blank") String> objectives = new ArrayList<>();

    @Valid
    @NotEmpty(message = "content blocks are required")
    private List<Map<String, Object>> content = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getObjectives() {
        return objectives;
    }

    public void setObjectives(List<String> objectives) {
        this.objectives = objectives;
    }

    public List<Map<String, Object>> getContent() {
        return content;
    }

    public void setContent(List<Map<String, Object>> content) {
        this.content = content;
    }
}
