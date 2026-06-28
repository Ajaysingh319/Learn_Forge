package com.learnforge.server.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateCourseRequest {
    @NotBlank(message = "title is required")
    private String title;
    private String description;
    @NotBlank(message = "creator is required")
    private String creator;
    private List<String> tags = new ArrayList<>();
    @Valid
    @NotEmpty(message = "at least one module is required")
    private List<ModulePayload> modules = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<ModulePayload> getModules() {
        return modules;
    }

    public void setModules(List<ModulePayload> modules) {
        this.modules = modules;
    }

    public static class ModulePayload {
        @NotBlank(message = "module title is required")
        private String title;
        @Valid
        @NotEmpty(message = "at least one lesson is required per module")
        private List<LessonPayload> lessons = new ArrayList<>();

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<LessonPayload> getLessons() {
            return lessons;
        }

        public void setLessons(List<LessonPayload> lessons) {
            this.lessons = lessons;
        }
    }

    public static class LessonPayload {
        @NotBlank(message = "lesson title is required")
        private String title;
        private List<String> objectives = new ArrayList<>();
        private List<Map<String, Object>> content = new ArrayList<>();
        private boolean enriched;

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

        public boolean isEnriched() {
            return enriched;
        }

        public void setEnriched(boolean enriched) {
            this.enriched = enriched;
        }
    }
}
