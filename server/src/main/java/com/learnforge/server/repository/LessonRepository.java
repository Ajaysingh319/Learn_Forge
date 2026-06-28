package com.learnforge.server.repository;

import com.learnforge.server.model.Lesson;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LessonRepository extends MongoRepository<Lesson, String> {
    List<Lesson> findByModuleId(String moduleId);
}
