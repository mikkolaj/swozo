package com.swozo.webservice.repository;

import com.swozo.databasemodel.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("from Course course  where course.teacher.id = :teacherId")
    List<Course> getTeacherCourses(Long teacherId);
}
