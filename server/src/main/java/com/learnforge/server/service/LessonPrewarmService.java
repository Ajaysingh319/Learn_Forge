package com.learnforge.server.service;

import com.learnforge.server.model.Course;
import com.learnforge.server.model.CourseModule;
import com.learnforge.server.repository.CourseModuleRepository;
import com.learnforge.server.repository.CourseRepository;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Generates and persists all lesson content for a course in the background right after it is
 * saved, so that opening a saved course later loads instantly without another AI call.
 * Failures (e.g. AI quota limits) are non-fatal: those lessons stay empty and are generated
 * lazily the first time they are opened.
 */
@Service
public class LessonPrewarmService {

    private static final Logger log = LoggerFactory.getLogger(LessonPrewarmService.class);

    private final CourseRepository courseRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final CourseService courseService;
    private final boolean enabled;
    private final long throttleMs;

    public LessonPrewarmService(CourseRepository courseRepository,
                                CourseModuleRepository courseModuleRepository,
                                CourseService courseService,
                                @Value("${app.lesson-prewarm.enabled:false}") boolean enabled,
                                @Value("${app.lesson-prewarm.throttle-ms:1500}") long throttleMs) {
        this.courseRepository = courseRepository;
        this.courseModuleRepository = courseModuleRepository;
        this.courseService = courseService;
        this.enabled = enabled;
        this.throttleMs = throttleMs;
    }

    @Async("lessonPrewarmExecutor")
    public void prewarmCourse(String courseId, String requesterSub) {
        if (!enabled || courseId == null) {
            return;
        }
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return;
        }

        int generated = 0;
        int failed = 0;
        for (String moduleId : safeList(course.getModuleIds())) {
            CourseModule module = courseModuleRepository.findById(moduleId).orElse(null);
            if (module == null) {
                continue;
            }
            for (String lessonId : safeList(module.getLessonIds())) {
                try {
                    courseService.generateAndSaveLesson(courseId, lessonId, requesterSub, false);
                    generated++;
                    if (throttleMs > 0) {
                        Thread.sleep(throttleMs);
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                } catch (RuntimeException ex) {
                    failed++;
                    log.warn("Prewarm failed for lesson {} (will generate lazily on open): {}",
                            lessonId, ex.getMessage());
                }
            }
        }
        log.info("Prewarm complete for course {}: {} lessons ready, {} deferred", courseId, generated, failed);
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? Collections.<T>emptyList() : list;
    }
}
