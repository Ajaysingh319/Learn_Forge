package com.learnforge.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GenerateLessonRequest {
    @NotBlank(message = "courseTitle is required")
    @Size(max = 180, message = "courseTitle must be at most 180 characters")
    private String courseTitle;

    @NotBlank(message = "moduleTitle is required")
    @Size(max = 180, message = "moduleTitle must be at most 180 characters")
    private String moduleTitle;

    @NotBlank(message = "lessonTitle is required")
    @Size(max = 180, message = "lessonTitle must be at most 180 characters")
    private String lessonTitle;

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getModuleTitle() {
        return moduleTitle;
    }

    public void setModuleTitle(String moduleTitle) {
        this.moduleTitle = moduleTitle;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }
}
