package com.learnforge.server.service;

import com.learnforge.server.dto.CourseResponse;
import com.learnforge.server.dto.CreateCourseRequest;

public interface CourseService {
    CourseResponse createCourse(CreateCourseRequest request);

    CourseResponse getCourseById(String courseId);
}
