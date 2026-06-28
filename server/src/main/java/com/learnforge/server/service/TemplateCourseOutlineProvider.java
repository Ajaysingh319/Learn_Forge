package com.learnforge.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnforge.server.dto.GeneratedCourseOutlineResponse;
import com.learnforge.server.exception.AiGenerationException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TemplateCourseOutlineProvider implements CourseOutlineAiProvider {

    private final ObjectMapper objectMapper;

    public TemplateCourseOutlineProvider(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String generateJsonOutline(String prompt) {
        String cleanedTopic = cleanTopic(prompt);
        GeneratedCourseOutlineResponse outline = new GeneratedCourseOutlineResponse();
        outline.setTitle("Text-to-Learn: " + cleanedTopic);
        outline.setDescription("A structured beginner-to-intermediate course on " + cleanedTopic + ".");
        outline.setTags(List.of(cleanedTopic, "foundations", "project-based"));
        outline.setModules(List.of(
                module("Introduction and Core Concepts", List.of(
                        "What is " + cleanedTopic + "?",
                        "Why " + cleanedTopic + " matters",
                        "Key terms and vocabulary"
                )),
                module("Practical Foundations", List.of(
                        "Setting up your learning workflow",
                        "Essential techniques in " + cleanedTopic,
                        "Common beginner mistakes"
                )),
                module("Intermediate Application", List.of(
                        "Building a mini project",
                        "Real-world examples and case studies",
                        "Debugging and improvement strategies"
                )),
                module("Advanced Direction", List.of(
                        "Advanced patterns in " + cleanedTopic,
                        "Performance and best practices",
                        "Next steps and learning roadmap"
                ))
        ));

        try {
            return objectMapper.writeValueAsString(outline);
        } catch (JsonProcessingException ex) {
            throw new AiGenerationException("Failed to generate template course outline", ex);
        }
    }

    private GeneratedCourseOutlineResponse.GeneratedModule module(String title, List<String> lessons) {
        GeneratedCourseOutlineResponse.GeneratedModule module = new GeneratedCourseOutlineResponse.GeneratedModule();
        module.setTitle(title);
        module.setLessons(lessons);
        return module;
    }

    private String cleanTopic(String topic) {
        if (topic == null || topic.isBlank()) {
            return "Selected Topic";
        }
        String trimmed = topic.trim();
        return trimmed.length() > 80 ? trimmed.substring(0, 80) : trimmed;
    }
}
