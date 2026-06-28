package com.learnforge.server.service;

import com.learnforge.server.dto.GeneratedLessonResponse;

public interface LessonGenerationService {
    GeneratedLessonResponse generateLesson(String courseTitle, String moduleTitle, String lessonTitle);
}
