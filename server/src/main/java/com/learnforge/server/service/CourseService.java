package com.learnforge.server.service;

import com.learnforge.server.dto.CourseResponse;
import com.learnforge.server.dto.CreateCourseRequest;
import java.util.List;

public interface CourseService {
    CourseResponse createCourse(CreateCourseRequest request, String creatorSub);

    CourseResponse getCourseById(String courseId);

    List<CourseResponse> getCoursesByCreator(String creatorSub);
}
