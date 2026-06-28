package com.learnforge.server.controller;

import com.learnforge.server.dto.GenerateCourseRequest;
import com.learnforge.server.dto.GeneratedCourseOutlineResponse;
import com.learnforge.server.service.CourseOutlineGenerationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api-base-path:/api}/ai")
public class AiController {

    private final CourseOutlineGenerationService courseOutlineGenerationService;

    public AiController(CourseOutlineGenerationService courseOutlineGenerationService) {
        this.courseOutlineGenerationService = courseOutlineGenerationService;
    }

    @PostMapping("/generate-course")
    public GeneratedCourseOutlineResponse generateCourse(@Valid @RequestBody GenerateCourseRequest request) {
        return courseOutlineGenerationService.generateCourseOutline(request.getTopic());
    }
}
