package com.learnforge.server.repository;

import com.learnforge.server.model.Course;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findByCreatorOrderByCreatedAtDesc(String creator);
}
