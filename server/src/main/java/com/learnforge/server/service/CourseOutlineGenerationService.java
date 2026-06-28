package com.learnforge.server.service;

import com.learnforge.server.dto.GeneratedCourseOutlineResponse;

public interface CourseOutlineGenerationService {
    GeneratedCourseOutlineResponse generateCourseOutline(String topic);
}
