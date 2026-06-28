package com.learnforge.server.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class GeneratedCourseOutlineResponse {
    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "description is required")
    private String description;

    private List<String> tags = new ArrayList<>();

    @Valid
    @NotEmpty(message = "modules are required")
    @Size(min = 3, max = 6, message = "modules must contain between 3 and 6 entries")
    private List<GeneratedModule> modules = new ArrayList<>();

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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<GeneratedModule> getModules() {
        return modules;
    }

    public void setModules(List<GeneratedModule> modules) {
        this.modules = modules;
    }

    public static class GeneratedModule {
        @NotBlank(message = "module title is required")
        private String title;

        @NotEmpty(message = "lessons are required")
        @Size(min = 3, max = 5, message = "lessons must contain between 3 and 5 entries")
        private List<@NotBlank(message = "lesson title is required") String> lessons = new ArrayList<>();

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getLessons() {
            return lessons;
        }

        public void setLessons(List<String> lessons) {
            this.lessons = lessons;
        }
    }
}
