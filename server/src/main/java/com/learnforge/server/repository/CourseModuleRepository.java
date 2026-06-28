package com.learnforge.server.repository;

import com.learnforge.server.model.CourseModule;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseModuleRepository extends MongoRepository<CourseModule, String> {
    List<CourseModule> findByCourseId(String courseId);
}
