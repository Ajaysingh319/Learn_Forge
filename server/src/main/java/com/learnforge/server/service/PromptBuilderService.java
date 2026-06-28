package com.learnforge.server.service;

import org.springframework.stereotype.Service;

@Service
public class PromptBuilderService {

    public String buildCourseOutlinePrompt(String topic) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert curriculum designer.\n");
        prompt.append("Generate a course outline for the topic below.\n\n");
        prompt.append("Topic: \"").append(topic).append("\"\n\n");
        prompt.append("Respond with raw JSON only. Do not include markdown fences.\n");
        prompt.append("Required JSON schema:\n");
        prompt.append("{\n");
        prompt.append("  \"title\": \"string\",\n");
        prompt.append("  \"description\": \"string\",\n");
        prompt.append("  \"tags\": [\"string\", \"string\"],\n");
        prompt.append("  \"modules\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"title\": \"string\",\n");
        prompt.append("      \"lessons\": [\"string\", \"string\", \"string\"]\n");
        prompt.append("    }\n");
        prompt.append("  ]\n");
        prompt.append("}\n\n");
        prompt.append("Rules:\n");
        prompt.append("- Include exactly 3 to 6 modules.\n");
        prompt.append("- Each module must include exactly 3 to 5 lesson titles.\n");
        prompt.append("- Curriculum should progress from fundamentals to advanced topics.\n");
        prompt.append("- Use concise and practical lesson titles.\n");
        return prompt.toString();
    }
}
