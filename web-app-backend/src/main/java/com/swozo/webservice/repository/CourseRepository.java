package com.swozo.webservice.repository;

import com.swozo.databasemodel.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

}
