package com.learnforge.server.service;

import org.springframework.stereotype.Service;

@Service
public class OpenAiCourseOutlineProvider implements CourseOutlineAiProvider {

    private final OpenAiClientService openAiClientService;

    public OpenAiCourseOutlineProvider(OpenAiClientService openAiClientService) {
        this.openAiClientService = openAiClientService;
    }

    @Override
    public String generateJsonOutline(String prompt) {
        return openAiClientService.generateJson(prompt);
    }
}
