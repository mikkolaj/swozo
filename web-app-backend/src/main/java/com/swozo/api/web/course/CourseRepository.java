package com.swozo.api.web.course;

import com.swozo.persistence.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> getCoursesByTeacherId(Long teacherId);

    @Query("from Course course join course.students cs where cs.id.userId = :studentId")
    List<Course> getCoursesByStudentsId(Long studentId);
}
