package com.learnforge.server.service;

import com.learnforge.server.config.AiProperties;
import com.learnforge.server.dto.GeneratedLessonResponse;
import org.springframework.stereotype.Service;

@Service
public class LessonGenerationServiceImpl implements LessonGenerationService {

    private final AiProperties aiProperties;
    private final PromptBuilderService promptBuilderService;
    private final OpenAiClientService openAiClientService;
    private final TemplateLessonProvider templateLessonProvider;
    private final LessonParserService lessonParserService;

    public LessonGenerationServiceImpl(AiProperties aiProperties,
                                       PromptBuilderService promptBuilderService,
                                       OpenAiClientService openAiClientService,
                                       TemplateLessonProvider templateLessonProvider,
                                       LessonParserService lessonParserService) {
        this.aiProperties = aiProperties;
        this.promptBuilderService = promptBuilderService;
        this.openAiClientService = openAiClientService;
        this.templateLessonProvider = templateLessonProvider;
        this.lessonParserService = lessonParserService;
    }

    @Override
    public GeneratedLessonResponse generateLesson(String courseTitle, String moduleTitle, String lessonTitle) {
        String prompt = promptBuilderService.buildLessonPrompt(courseTitle, moduleTitle, lessonTitle);
        String provider = aiProperties.getProvider() == null ? "template" : aiProperties.getProvider().trim().toLowerCase();
        String rawJson;
        if ("openai".equals(provider)) {
            rawJson = openAiClientService.generateJson(prompt);
        } else {
            rawJson = templateLessonProvider.generateJsonLesson(courseTitle, moduleTitle, lessonTitle);
        }
        return lessonParserService.parseAndValidate(rawJson);
    }
}
