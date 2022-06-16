package com.swozo.webservice.repository;

import com.swozo.databasemodel.Course;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<1, 2>    1 - mapped class, 2 - id format
public interface CourseRepository extends JpaRepository<Course, Long> {

}
