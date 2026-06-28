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

    public String buildLessonPrompt(String courseTitle, String moduleTitle, String lessonTitle) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert instructional designer.\n");
        prompt.append("Generate detailed lesson content for the lesson below.\n\n");
        prompt.append("Course: \"").append(courseTitle).append("\"\n");
        prompt.append("Module: \"").append(moduleTitle).append("\"\n");
        prompt.append("Lesson: \"").append(lessonTitle).append("\"\n\n");
        prompt.append("Respond with raw JSON only. Do not include markdown fences.\n");
        prompt.append("Required JSON schema:\n");
        prompt.append("{\n");
        prompt.append("  \"title\": \"string\",\n");
        prompt.append("  \"objectives\": [\"string\", \"string\"],\n");
        prompt.append("  \"content\": [\n");
        prompt.append("    { \"type\": \"heading\", \"text\": \"string\" },\n");
        prompt.append("    { \"type\": \"paragraph\", \"text\": \"string\" },\n");
        prompt.append("    { \"type\": \"code\", \"language\": \"string\", \"text\": \"string\" },\n");
        prompt.append("    { \"type\": \"video\", \"query\": \"string\" },\n");
        prompt.append("    { \"type\": \"mcq\", \"question\": \"string\", \"options\": [\"A\", \"B\", \"C\", \"D\"], \"answer\": 0, \"explanation\": \"string\" }\n");
        prompt.append("  ]\n");
        prompt.append("}\n\n");
        prompt.append("Rules:\n");
        prompt.append("- Include clear lesson objectives.\n");
        prompt.append("- Use content block types: heading, paragraph, code (only if relevant), video, mcq.\n");
        prompt.append("- Video blocks must use a search query in the \"query\" field, not a direct URL.\n");
        prompt.append("- Include 4 to 5 MCQ blocks at the end of the content array.\n");
        prompt.append("- Each MCQ must include question, options, answer index, and explanation.\n");
        prompt.append("- Keep explanations concise and educational.\n");
        return prompt.toString();
    }
}
