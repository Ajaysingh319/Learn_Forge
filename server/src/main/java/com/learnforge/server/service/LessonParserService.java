package com.learnforge.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnforge.server.dto.GeneratedLessonResponse;
import com.learnforge.server.exception.BadRequestException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class LessonParserService {

    private static final List<String> ALLOWED_BLOCK_TYPES = Arrays.asList(
            "heading", "paragraph", "code", "video", "mcq"
    );

    private final ObjectMapper objectMapper;
    private final Validator validator;

    public LessonParserService(ObjectMapper objectMapper, Validator validator) {
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    public GeneratedLessonResponse parseAndValidate(String rawJson) {
        String sanitized = sanitizeJson(rawJson);
        GeneratedLessonResponse response;
        try {
            response = objectMapper.readValue(sanitized, GeneratedLessonResponse.class);
        } catch (JsonProcessingException ex) {
            throw new BadRequestException("AI response is not valid JSON");
        }

        Set<ConstraintViolation<GeneratedLessonResponse>> violations = validator.validate(response);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            throw new BadRequestException("AI response failed schema validation: " + message);
        }

        validateContentBlocks(response.getContent());
        validateMcqCount(response.getContent());
        return response;
    }

    private void validateContentBlocks(List<Map<String, Object>> content) {
        for (int i = 0; i < content.size(); i++) {
            Map<String, Object> block = content.get(i);
            Object typeObj = block.get("type");
            if (!(typeObj instanceof String) || ((String) typeObj).trim().isEmpty()) {
                throw new BadRequestException("Content block " + (i + 1) + " is missing a valid type");
            }
            String type = ((String) typeObj).trim().toLowerCase();
            if (!ALLOWED_BLOCK_TYPES.contains(type)) {
                throw new BadRequestException("Content block " + (i + 1) + " has unsupported type: " + type);
            }

            switch (type) {
                case "heading":
                case "paragraph":
                    requireText(block, i + 1);
                    break;
                case "code":
                    requireText(block, i + 1);
                    requireField(block, i + 1, "language");
                    break;
                case "video":
                    requireField(block, i + 1, "query");
                    break;
                case "mcq":
                    requireField(block, i + 1, "question");
                    requireOptions(block, i + 1);
                    requireAnswer(block, i + 1);
                    requireField(block, i + 1, "explanation");
                    break;
                default:
                    break;
            }
        }
    }

    private void validateMcqCount(List<Map<String, Object>> content) {
        int mcqCount = 0;
        for (Map<String, Object> block : content) {
            Object typeObj = block.get("type");
            if (typeObj instanceof String && "mcq".equalsIgnoreCase((String) typeObj)) {
                mcqCount++;
            }
        }
        if (mcqCount < 4 || mcqCount > 5) {
            throw new BadRequestException("Lesson must include between 4 and 5 MCQ blocks");
        }
    }

    private void requireText(Map<String, Object> block, int index) {
        Object textObj = block.get("text");
        if (!(textObj instanceof String) || ((String) textObj).trim().isEmpty()) {
            throw new BadRequestException("Content block " + index + " is missing text");
        }
    }

    private void requireField(Map<String, Object> block, int index, String field) {
        Object value = block.get(field);
        if (!(value instanceof String) || ((String) value).trim().isEmpty()) {
            throw new BadRequestException("Content block " + index + " is missing " + field);
        }
    }

    @SuppressWarnings("unchecked")
    private void requireOptions(Map<String, Object> block, int index) {
        Object optionsObj = block.get("options");
        if (!(optionsObj instanceof List<?>)) {
            throw new BadRequestException("Content block " + index + " must include options array");
        }
        List<?> options = (List<?>) optionsObj;
        if (options.size() < 2) {
            throw new BadRequestException("Content block " + index + " must include at least 2 options");
        }
        for (Object option : options) {
            if (!(option instanceof String) || ((String) option).trim().isEmpty()) {
                throw new BadRequestException("Content block " + index + " has invalid option values");
            }
        }
    }

    private void requireAnswer(Map<String, Object> block, int index) {
        Object answerObj = block.get("answer");
        if (!(answerObj instanceof Number)) {
            throw new BadRequestException("Content block " + index + " must include numeric answer index");
        }
        int answer = ((Number) answerObj).intValue();
        Object optionsObj = block.get("options");
        if (optionsObj instanceof List<?>) {
            List<?> options = (List<?>) optionsObj;
            if (answer < 0 || answer >= options.size()) {
                throw new BadRequestException("Content block " + index + " has out-of-range answer index");
            }
        }
    }

    private String sanitizeJson(String rawJson) {
        if (rawJson == null || rawJson.trim().isEmpty()) {
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
