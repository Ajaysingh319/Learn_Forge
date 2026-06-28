package com.learnforge.server.controller;

import com.learnforge.server.dto.GenerateCourseRequest;
import com.learnforge.server.dto.GenerateLessonRequest;
import com.learnforge.server.dto.GeneratedCourseOutlineResponse;
import com.learnforge.server.dto.GeneratedLessonResponse;
import com.learnforge.server.service.CourseOutlineGenerationService;
import com.learnforge.server.service.LessonGenerationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api-base-path:/api}/ai")
public class AiController {

    private final CourseOutlineGenerationService courseOutlineGenerationService;
    private final LessonGenerationService lessonGenerationService;

    public AiController(CourseOutlineGenerationService courseOutlineGenerationService,
                        LessonGenerationService lessonGenerationService) {
        this.courseOutlineGenerationService = courseOutlineGenerationService;
        this.lessonGenerationService = lessonGenerationService;
    }

    @PostMapping("/generate-course")
    public GeneratedCourseOutlineResponse generateCourse(@Valid @RequestBody GenerateCourseRequest request) {
        return courseOutlineGenerationService.generateCourseOutline(request.getTopic());
    }

    @PostMapping("/generate-lesson")
    public GeneratedLessonResponse generateLesson(@Valid @RequestBody GenerateLessonRequest request) {
        return lessonGenerationService.generateLesson(
                request.getCourseTitle(),
                request.getModuleTitle(),
                request.getLessonTitle()
        );
    }
}
