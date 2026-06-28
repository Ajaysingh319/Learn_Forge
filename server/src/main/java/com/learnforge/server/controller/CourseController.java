package com.learnforge.server.controller;

import com.learnforge.server.dto.CourseResponse;
import com.learnforge.server.dto.CourseSummaryResponse;
import com.learnforge.server.dto.CreateCourseRequest;
import com.learnforge.server.dto.GeneratedCourseOutlineResponse;
import com.learnforge.server.service.CourseService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api-base-path:/api}/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse createCourse(@Valid @RequestBody CreateCourseRequest request,
                                       @AuthenticationPrincipal Jwt jwt) {
        return courseService.createCourse(request, jwt.getSubject());
    }

    @PostMapping("/save-outline")
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse saveGeneratedOutline(@Valid @RequestBody GeneratedCourseOutlineResponse outline,
                                               @AuthenticationPrincipal Jwt jwt) {
        return courseService.saveGeneratedOutline(outline, jwt.getSubject());
    }

    @GetMapping("/my")
    public List<CourseSummaryResponse> getMyCourseSummaries(@AuthenticationPrincipal Jwt jwt) {
        return courseService.getCourseSummariesByCreator(jwt.getSubject());
    }

    @GetMapping("/my/full")
    public List<CourseResponse> getMyCourses(@AuthenticationPrincipal Jwt jwt) {
        return courseService.getCoursesByCreator(jwt.getSubject());
    }

    @GetMapping("/{courseId}")
    public CourseResponse getCourseById(@PathVariable String courseId,
                                        @AuthenticationPrincipal Jwt jwt) {
        return courseService.getCourseById(courseId, jwt.getSubject());
    }
}
