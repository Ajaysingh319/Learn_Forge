package com.learnforge.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnforge.server.dto.GeneratedCourseOutlineResponse;
import com.learnforge.server.exception.BadRequestException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CourseOutlineParserService {

    private final ObjectMapper objectMapper;
    private final Validator validator;

    public CourseOutlineParserService(ObjectMapper objectMapper, Validator validator) {
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    public GeneratedCourseOutlineResponse parseAndValidate(String rawJson) {
        String sanitized = sanitizeJson(rawJson);
        GeneratedCourseOutlineResponse response;
        try {
            response = objectMapper.readValue(sanitized, GeneratedCourseOutlineResponse.class);
        } catch (JsonProcessingException ex) {
            throw new BadRequestException("AI response is not valid JSON");
        }

        Set<ConstraintViolation<GeneratedCourseOutlineResponse>> violations = validator.validate(response);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            throw new BadRequestException("AI response failed schema validation: " + message);
        }

        validateModuleLessonCounts(response);
        return response;
    }

    private void validateModuleLessonCounts(GeneratedCourseOutlineResponse response) {
        List<GeneratedCourseOutlineResponse.GeneratedModule> modules = response.getModules();
        if (modules.size() < 3 || modules.size() > 6) {
            throw new BadRequestException("Generated outline must contain between 3 and 6 modules");
        }
        for (int i = 0; i < modules.size(); i++) {
            int lessonCount = modules.get(i).getLessons().size();
            if (lessonCount < 3 || lessonCount > 5) {
                throw new BadRequestException("Module " + (i + 1) + " must contain between 3 and 5 lessons");
            }
        }
    }

    private String sanitizeJson(String rawJson) {
        if (rawJson == null || rawJson.isBlank()) {
            throw new BadRequestException("AI response is empty");
        }
        String trimmed = rawJson.trim();
        if (trimmed.startsWith("```")) {
            int firstLineBreak = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstLineBreak > 0 && lastFence > firstLineBreak) {
                return trimmed.substring(firstLineBreak + 1, lastFence).trim();
            }
        }
        return trimmed;
    }
}
