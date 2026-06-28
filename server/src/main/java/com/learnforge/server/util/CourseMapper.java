package com.learnforge.server.util;

import com.learnforge.server.dto.CreateCourseRequest;
import com.learnforge.server.dto.GeneratedCourseOutlineResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CourseMapper {

    private CourseMapper() {
    }

    public static CreateCourseRequest fromGeneratedOutline(GeneratedCourseOutlineResponse outline) {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle(outline.getTitle());
        request.setDescription(outline.getDescription());
        request.setTags(outline.getTags() == null ? new ArrayList<String>() : new ArrayList<String>(outline.getTags()));

        List<CreateCourseRequest.ModulePayload> modules = new ArrayList<CreateCourseRequest.ModulePayload>();
        for (GeneratedCourseOutlineResponse.GeneratedModule generatedModule : outline.getModules()) {
            CreateCourseRequest.ModulePayload modulePayload = new CreateCourseRequest.ModulePayload();
            modulePayload.setTitle(generatedModule.getTitle());

            List<CreateCourseRequest.LessonPayload> lessons = new ArrayList<CreateCourseRequest.LessonPayload>();
            for (String lessonTitle : generatedModule.getLessons()) {
                CreateCourseRequest.LessonPayload lessonPayload = new CreateCourseRequest.LessonPayload();
                lessonPayload.setTitle(lessonTitle);
                lessonPayload.setObjectives(Collections.<String>emptyList());
                lessonPayload.setContent(Collections.<java.util.Map<String, Object>>emptyList());
                lessonPayload.setEnriched(false);
                lessons.add(lessonPayload);
            }
            modulePayload.setLessons(lessons);
            modules.add(modulePayload);
        }
        request.setModules(modules);
        return request;
    }
}
