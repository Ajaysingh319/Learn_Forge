package com.learnforge.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnforge.server.dto.GeneratedLessonResponse;
import com.learnforge.server.exception.AiGenerationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class TemplateLessonProvider {

    private final ObjectMapper objectMapper;

    public TemplateLessonProvider(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String generateJsonLesson(String courseTitle, String moduleTitle, String lessonTitle) {
        GeneratedLessonResponse lesson = new GeneratedLessonResponse();
        lesson.setTitle(lessonTitle);
        lesson.setObjectives(Arrays.asList(
                "Understand the core ideas in " + lessonTitle,
                "Identify key concepts within " + moduleTitle,
                "Apply foundational knowledge from " + courseTitle
        ));

        List<Map<String, Object>> content = new ArrayList<Map<String, Object>>();
        content.add(block("heading", lessonTitle));
        content.add(block("paragraph",
                "This lesson introduces " + lessonTitle + " as part of " + moduleTitle
                        + " in the course " + courseTitle + ". You will learn practical concepts and examples."));
        content.add(block("code", "javascript", "console.log('Learning: " + sanitize(lessonTitle) + "');"));
        content.add(videoBlock("Educational video about " + lessonTitle));
        content.add(block("paragraph",
                "Review the examples above and focus on how each concept connects to real-world use cases."));
        content.add(mcq(
                "What is the primary focus of this lesson?",
                Arrays.asList("Advanced deployment", lessonTitle, "Unrelated history", "Database indexing"),
                1,
                "The lesson is centered on " + lessonTitle + " within the current module."
        ));
        content.add(mcq(
                "Which module does this lesson belong to?",
                Arrays.asList("Unknown module", moduleTitle, "Marketing basics", "Finance fundamentals"),
                1,
                "This lesson is part of " + moduleTitle + "."
        ));
        content.add(mcq(
                "Why are objectives important?",
                Arrays.asList("They replace all content", "They guide learning outcomes", "They remove quizzes", "They hide progress"),
                1,
                "Objectives define what learners should be able to do after completing the lesson."
        ));
        content.add(mcq(
                "What should you do after reading this lesson?",
                Arrays.asList("Skip practice", "Apply concepts with examples", "Delete notes", "Ignore key terms"),
                1,
                "Applying concepts through examples reinforces understanding and retention."
        ));

        lesson.setContent(content);

        try {
            return objectMapper.writeValueAsString(lesson);
        } catch (JsonProcessingException ex) {
            throw new AiGenerationException("Failed to generate template lesson content", ex);
        }
    }

    private Map<String, Object> block(String type, String text) {
        Map<String, Object> block = new HashMap<String, Object>();
        block.put("type", type);
        block.put("text", text);
        return block;
    }

    private Map<String, Object> block(String type, String language, String text) {
        Map<String, Object> block = new HashMap<String, Object>();
        block.put("type", type);
        block.put("language", language);
        block.put("text", text);
        return block;
    }

    private Map<String, Object> videoBlock(String query) {
        Map<String, Object> block = new HashMap<String, Object>();
        block.put("type", "video");
        block.put("query", query);
        return block;
    }

    private Map<String, Object> mcq(String question, List<String> options, int answer, String explanation) {
        Map<String, Object> block = new HashMap<String, Object>();
        block.put("type", "mcq");
        block.put("question", question);
        block.put("options", options);
        block.put("answer", answer);
        block.put("explanation", explanation);
        return block;
    }

    private String sanitize(String value) {
        return value == null ? "" : value.replace("'", "");
    }
}
