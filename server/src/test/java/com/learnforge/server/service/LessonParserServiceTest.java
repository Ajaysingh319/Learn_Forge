package com.learnforge.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnforge.server.dto.GeneratedLessonResponse;
import com.learnforge.server.exception.BadRequestException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LessonParserServiceTest {

    private LessonParserService parserService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        parserService = new LessonParserService(objectMapper, validator);
    }

    @Test
    void parseAndValidate_acceptsValidLesson() {
        String json = """
                {
                  "title": "Intro to Hooks",
                  "objectives": ["Understand useState", "Apply useEffect"],
                  "content": [
                    {"type": "heading", "text": "Overview"},
                    {"type": "paragraph", "text": "Hooks let functional components use state."},
                    {"type": "code", "language": "javascript", "text": "const [count, setCount] = useState(0);"},
                    {"type": "video", "query": "React hooks tutorial"},
                    {"type": "mcq", "question": "Q1?", "options": ["A", "B"], "answer": 1, "explanation": "Because B."},
                    {"type": "mcq", "question": "Q2?", "options": ["A", "B"], "answer": 0, "explanation": "Because A."},
                    {"type": "mcq", "question": "Q3?", "options": ["A", "B"], "answer": 1, "explanation": "Because B."},
                    {"type": "mcq", "question": "Q4?", "options": ["A", "B"], "answer": 0, "explanation": "Because A."}
                  ],
                  "resources": [
                    {"title": "React Hooks docs", "url": "https://react.dev/reference/react"},
                    {"title": "useState guide", "url": "https://react.dev/reference/react/useState"}
                  ]
                }
                """;

        GeneratedLessonResponse response = parserService.parseAndValidate(json);

        assertEquals("Intro to Hooks", response.getTitle());
        assertEquals(8, response.getContent().size());
        assertEquals(2, response.getResources().size());
    }

    @Test
    void parseAndValidate_rejectsTooFewMcqs() {
        String json = """
                {
                  "title": "Incomplete Lesson",
                  "objectives": ["Learn something"],
                  "content": [
                    {"type": "heading", "text": "Overview"},
                    {"type": "mcq", "question": "Q1?", "options": ["A", "B"], "answer": 0, "explanation": "A"}
                  ],
                  "resources": [
                    {"title": "Reference", "url": "https://example.com/docs"}
                  ]
                }
                """;

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> parserService.parseAndValidate(json)
        );

        assertEquals("Lesson must include between 4 and 5 MCQ blocks", ex.getMessage());
    }

    @Test
    void parseAndValidate_rejectsUnsupportedBlockType() {
        String json = """
                {
                  "title": "Bad Blocks",
                  "objectives": ["Learn something"],
                  "content": [
                    {"type": "quiz", "text": "Unsupported"}
                  ],
                  "resources": [
                    {"title": "Reference", "url": "https://example.com/docs"}
                  ]
                }
                """;

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> parserService.parseAndValidate(json)
        );

        assertEquals("Content block 1 has unsupported type: quiz", ex.getMessage());
    }

    @Test
    void parseAndValidate_rejectsMissingResources() {
        String json = """
                {
                  "title": "No Resources",
                  "objectives": ["Learn something"],
                  "content": [
                    {"type": "heading", "text": "Overview"},
                    {"type": "mcq", "question": "Q1?", "options": ["A", "B"], "answer": 0, "explanation": "A"},
                    {"type": "mcq", "question": "Q2?", "options": ["A", "B"], "answer": 1, "explanation": "B"},
                    {"type": "mcq", "question": "Q3?", "options": ["A", "B"], "answer": 0, "explanation": "A"},
                    {"type": "mcq", "question": "Q4?", "options": ["A", "B"], "answer": 1, "explanation": "B"}
                  ]
                }
                """;

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> parserService.parseAndValidate(json)
        );

        org.junit.jupiter.api.Assertions.assertTrue(ex.getMessage().contains("resources"));
    }

    @Test
    void parseAndValidate_rejectsResourceWithoutUrl() {
        String json = """
                {
                  "title": "Bad Resource",
                  "objectives": ["Learn something"],
                  "content": [
                    {"type": "heading", "text": "Overview"},
                    {"type": "mcq", "question": "Q1?", "options": ["A", "B"], "answer": 0, "explanation": "A"},
                    {"type": "mcq", "question": "Q2?", "options": ["A", "B"], "answer": 1, "explanation": "B"},
                    {"type": "mcq", "question": "Q3?", "options": ["A", "B"], "answer": 0, "explanation": "A"},
                    {"type": "mcq", "question": "Q4?", "options": ["A", "B"], "answer": 1, "explanation": "B"}
                  ],
                  "resources": [
                    {"title": "Missing URL"}
                  ]
                }
                """;

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> parserService.parseAndValidate(json)
        );

        assertEquals("Resource 1 is missing a url", ex.getMessage());
    }
}
