package com.learnforge.server.service;

import com.learnforge.server.dto.CourseResponse;
import com.learnforge.server.dto.CourseSummaryResponse;
import com.learnforge.server.dto.CreateCourseRequest;
import com.learnforge.server.dto.GeneratedCourseOutlineResponse;
import com.learnforge.server.exception.ForbiddenException;
import com.learnforge.server.exception.ResourceNotFoundException;
import com.learnforge.server.model.Course;
import com.learnforge.server.model.CourseModule;
import com.learnforge.server.model.Lesson;
import com.learnforge.server.repository.CourseModuleRepository;
import com.learnforge.server.repository.CourseRepository;
import com.learnforge.server.repository.LessonRepository;
import com.learnforge.server.util.CourseMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final LessonRepository lessonRepository;

    public CourseServiceImpl(CourseRepository courseRepository,
                             CourseModuleRepository courseModuleRepository,
                             LessonRepository lessonRepository) {
        this.courseRepository = courseRepository;
        this.courseModuleRepository = courseModuleRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    public CourseResponse createCourse(CreateCourseRequest request, String creatorSub) {
        Instant now = Instant.now();
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setCreator(creatorSub);
        course.setTags(new ArrayList<>(safeList(request.getTags())));
        course.setCreatedAt(now);
        course.setUpdatedAt(now);
        course = courseRepository.save(course);

        List<String> moduleIds = new ArrayList<String>();
        for (CreateCourseRequest.ModulePayload modulePayload : request.getModules()) {
            CourseModule module = new CourseModule();
            module.setTitle(modulePayload.getTitle());
            module.setCourseId(course.getId());
            module.setCreatedAt(now);
            module.setUpdatedAt(now);
            module = courseModuleRepository.save(module);

            List<String> lessonIds = new ArrayList<String>();
            for (CreateCourseRequest.LessonPayload lessonPayload : modulePayload.getLessons()) {
                Lesson lesson = new Lesson();
                lesson.setTitle(lessonPayload.getTitle());
                lesson.setObjectives(new ArrayList<>(safeList(lessonPayload.getObjectives())));
                lesson.setContent(new ArrayList<>(safeList(lessonPayload.getContent())));
                lesson.setResources(new ArrayList<>(safeList(lessonPayload.getResources())));
                lesson.setEnriched(lessonPayload.isEnriched());
                lesson.setModuleId(module.getId());
                lesson.setCreatedAt(now);
                lesson.setUpdatedAt(now);
                lesson = lessonRepository.save(lesson);
                lessonIds.add(lesson.getId());
            }

            module.setLessonIds(lessonIds);
            module.setUpdatedAt(Instant.now());
            courseModuleRepository.save(module);
            moduleIds.add(module.getId());
        }

        course.setModuleIds(moduleIds);
        course.setUpdatedAt(Instant.now());
        courseRepository.save(course);

        return buildCourseResponse(course);
    }

    @Override
    public CourseResponse saveGeneratedOutline(GeneratedCourseOutlineResponse outline, String creatorSub) {
        CreateCourseRequest request = CourseMapper.fromGeneratedOutline(outline);
        return createCourse(request, creatorSub);
    }

    @Override
    public CourseResponse getCourseById(String courseId, String requesterSub) {
        Course course = findCourseOrThrow(courseId);
        assertOwner(course, requesterSub);
        return buildCourseResponse(course);
    }

    @Override
    public List<CourseResponse> getCoursesByCreator(String creatorSub) {
        List<Course> courses = courseRepository.findByCreatorOrderByCreatedAtDesc(creatorSub);
        List<CourseResponse> responses = new ArrayList<CourseResponse>();
        for (Course course : courses) {
            responses.add(buildCourseResponse(course));
        }
        return responses;
    }

    @Override
    public List<CourseSummaryResponse> getCourseSummariesByCreator(String creatorSub) {
        List<Course> courses = courseRepository.findByCreatorOrderByCreatedAtDesc(creatorSub);
        List<CourseSummaryResponse> summaries = new ArrayList<CourseSummaryResponse>();
        for (Course course : courses) {
            summaries.add(buildCourseSummary(course));
        }
        return summaries;
    }

    private Course findCourseOrThrow(String courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
    }

    private void assertOwner(Course course, String requesterSub) {
        if (course.getCreator() == null || !course.getCreator().equals(requesterSub)) {
            throw new ForbiddenException("You do not have access to this course");
        }
    }

    private CourseSummaryResponse buildCourseSummary(Course course) {
        CourseSummaryResponse summary = new CourseSummaryResponse();
        summary.setId(course.getId());
        summary.setTitle(course.getTitle());
        summary.setDescription(course.getDescription());
        summary.setCreator(course.getCreator());
        summary.setTags(new ArrayList<>(safeList(course.getTags())));
        summary.setCreatedAt(course.getCreatedAt());
        summary.setUpdatedAt(course.getUpdatedAt());

        List<String> moduleIds = safeList(course.getModuleIds());
        summary.setModuleCount(moduleIds.size());

        int lessonCount = 0;
        if (!moduleIds.isEmpty()) {
            List<CourseModule> modules = courseModuleRepository.findAllById(moduleIds);
            for (CourseModule module : modules) {
                lessonCount += safeList(module.getLessonIds()).size();
            }
        }
        summary.setLessonCount(lessonCount);
        return summary;
    }

    private CourseResponse buildCourseResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setTitle(course.getTitle());
        response.setDescription(course.getDescription());
        response.setCreator(course.getCreator());
        response.setTags(new ArrayList<>(safeList(course.getTags())));
        response.setCreatedAt(course.getCreatedAt());
        response.setUpdatedAt(course.getUpdatedAt());

        List<CourseModule> modules = sortedByIds(
                courseModuleRepository.findAllById(safeList(course.getModuleIds())),
                CourseModule::getId,
                safeList(course.getModuleIds())
        );

        List<CourseResponse.ModuleResponse> moduleResponses = new ArrayList<CourseResponse.ModuleResponse>();
        for (CourseModule module : modules) {
            CourseResponse.ModuleResponse moduleResponse = new CourseResponse.ModuleResponse();
            moduleResponse.setId(module.getId());
            moduleResponse.setTitle(module.getTitle());
            moduleResponse.setCreatedAt(module.getCreatedAt());
            moduleResponse.setUpdatedAt(module.getUpdatedAt());

            List<Lesson> lessons = sortedByIds(
                    lessonRepository.findAllById(safeList(module.getLessonIds())),
                    Lesson::getId,
                    safeList(module.getLessonIds())
            );

            List<CourseResponse.LessonResponse> lessonResponses = new ArrayList<CourseResponse.LessonResponse>();
            for (Lesson lesson : lessons) {
                CourseResponse.LessonResponse lessonResponse = new CourseResponse.LessonResponse();
                lessonResponse.setId(lesson.getId());
                lessonResponse.setTitle(lesson.getTitle());
                lessonResponse.setObjectives(new ArrayList<>(safeList(lesson.getObjectives())));
                lessonResponse.setContent(new ArrayList<>(safeList(lesson.getContent())));
                lessonResponse.setResources(new ArrayList<>(safeList(lesson.getResources())));
                lessonResponse.setEnriched(lesson.isEnriched());
                lessonResponse.setCreatedAt(lesson.getCreatedAt());
                lessonResponse.setUpdatedAt(lesson.getUpdatedAt());
                lessonResponses.add(lessonResponse);
            }

            moduleResponse.setLessons(lessonResponses);
            moduleResponses.add(moduleResponse);
        }

        response.setModules(moduleResponses);
        return response;
    }

    private <T> List<T> sortedByIds(List<T> source, Function<T, String> idSelector, List<String> orderedIds) {
        Map<String, Integer> orderIndex = new HashMap<String, Integer>();
        for (int i = 0; i < orderedIds.size(); i++) {
            orderIndex.put(orderedIds.get(i), i);
        }
        source.sort((left, right) -> {
            Integer leftIndex = orderIndex.getOrDefault(idSelector.apply(left), Integer.MAX_VALUE);
            Integer rightIndex = orderIndex.getOrDefault(idSelector.apply(right), Integer.MAX_VALUE);
            return Integer.compare(leftIndex, rightIndex);
        });
        return source;
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? Collections.<T>emptyList() : list;
    }
}
