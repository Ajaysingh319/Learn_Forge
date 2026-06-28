package com.learnforge.server.service;

import com.learnforge.server.dto.CourseResponse;
import com.learnforge.server.dto.CourseSummaryResponse;
import com.learnforge.server.dto.CreateCourseRequest;
import com.learnforge.server.dto.GeneratedCourseOutlineResponse;
import java.util.List;

public interface CourseService {
    CourseResponse createCourse(CreateCourseRequest request, String creatorSub);

    CourseResponse saveGeneratedOutline(GeneratedCourseOutlineResponse outline, String creatorSub);

    CourseResponse getCourseById(String courseId, String requesterSub);

    List<CourseResponse> getCoursesByCreator(String creatorSub);

    List<CourseSummaryResponse> getCourseSummariesByCreator(String creatorSub);
}
