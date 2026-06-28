package com.learnforge.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnforge.server.dto.GeneratedCourseOutlineResponse;
import com.learnforge.server.exception.BadRequestException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CourseOutlineParserServiceTest {

    private CourseOutlineParserService parserService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        parserService = new CourseOutlineParserService(objectMapper, validator);
    }

    @Test
    void parseAndValidate_acceptsValidOutline() {
        String json = """
                {
                  "title": "React Basics",
                  "description": "Learn React fundamentals",
                  "tags": ["react"],
                  "modules": [
                    {"title": "Intro", "lessons": ["Setup", "JSX", "Components"]},
                    {"title": "Hooks", "lessons": ["useState", "useEffect", "Custom Hooks"]},
                    {"title": "State", "lessons": ["Lifting State", "Context", "Reducer"]}
                  ]
                }
                """;

        GeneratedCourseOutlineResponse response = parserService.parseAndValidate(json);

        assertEquals("React Basics", response.getTitle());
        assertEquals(3, response.getModules().size());
    }

    @Test
    void parseAndValidate_stripsMarkdownFence() {
        String json = """
                ```json
                {
                  "title": "Node.js Basics",
                  "description": "Learn Node.js",
                  "modules": [
                    {"title": "Intro", "lessons": ["Runtime", "Modules", "NPM"]},
                    {"title": "HTTP", "lessons": ["Server", "Routing", "Middleware"]},
                    {"title": "Data", "lessons": ["Files", "JSON", "Streams"]}
                  ]
                }
                ```
                """;

        GeneratedCourseOutlineResponse response = parserService.parseAndValidate(json);

        assertEquals("Node.js Basics", response.getTitle());
    }

    @Test
    void parseAndValidate_rejectsTooFewModules() {
        String json = """
                {
                  "title": "Short Course",
                  "description": "Too short",
                  "modules": [
                    {"title": "Only Module", "lessons": ["One", "Two", "Three"]}
                  ]
                }
                """;

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> parserService.parseAndValidate(json)
        );

        assertEquals(true, ex.getMessage().contains("modules"));
    }

    @Test
    void parseAndValidate_rejectsInvalidJson() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> parserService.parseAndValidate("{title: broken")
        );

        assertEquals("AI response is not valid JSON", ex.getMessage());
    }
}
