package com.learnforge.server.service;

import com.learnforge.server.config.AiProperties;
import com.learnforge.server.dto.GeneratedCourseOutlineResponse;
import org.springframework.stereotype.Service;

@Service
public class CourseOutlineGenerationServiceImpl implements CourseOutlineGenerationService {

    private final AiProperties aiProperties;
    private final PromptBuilderService promptBuilderService;
    private final OpenAiCourseOutlineProvider openAiProvider;
    private final GeminiClientService geminiClientService;
    private final TemplateCourseOutlineProvider templateProvider;
    private final CourseOutlineParserService parserService;

    public CourseOutlineGenerationServiceImpl(AiProperties aiProperties,
                                              PromptBuilderService promptBuilderService,
                                              OpenAiCourseOutlineProvider openAiProvider,
                                              GeminiClientService geminiClientService,
                                              TemplateCourseOutlineProvider templateProvider,
                                              CourseOutlineParserService parserService) {
        this.aiProperties = aiProperties;
        this.promptBuilderService = promptBuilderService;
        this.openAiProvider = openAiProvider;
        this.geminiClientService = geminiClientService;
        this.templateProvider = templateProvider;
        this.parserService = parserService;
    }

    @Override
    public GeneratedCourseOutlineResponse generateCourseOutline(String topic) {
        String prompt = promptBuilderService.buildCourseOutlinePrompt(topic);
        String provider = aiProperties.getProvider() == null ? "template" : aiProperties.getProvider().trim().toLowerCase();
        String rawJson;
        if ("openai".equals(provider)) {
            rawJson = openAiProvider.generateJsonOutline(prompt);
        } else if ("gemini".equals(provider)) {
            rawJson = geminiClientService.generateText(prompt);
        } else {
            rawJson = templateProvider.generateJsonOutline(topic);
        }
        return parserService.parseAndValidate(rawJson);
    }
}
