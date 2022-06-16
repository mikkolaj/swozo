package com.swozo.api.web;

import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.Course;
import com.swozo.databasemodel.users.User;
import com.swozo.security.AccessToken;
import com.swozo.webservice.service.CourseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping("/courses")
@SecurityRequirement(name = ACCESS_TOKEN)
public class CourseController {
    private final Logger logger = LoggerFactory.getLogger(CourseController.class);
    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/all-system-courses")
    @PreAuthorize("hasRole('ADMIN')")
    public Collection<Course> getAllSystemCourses(AccessToken token) {
        return courseService.getAllCourses();
    }

    @GetMapping("/all-courses")
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    public Collection<Course> getUserCourses(AccessToken token) {
        Long userId = token.getUserId();
        logger.info("course list for user with id: {}", userId);
        return courseService.getUserCourses(userId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public Course getCourse(AccessToken token, @PathVariable Long id) {
        logger.info("course info getter for id: {}", id);
        return courseService.getCourse(id);
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public Course addCourse(AccessToken token, @RequestBody Course course) {
        logger.info("creating new course with name: {}", course.getName());
        return courseService.createCourse(course, token.getUserId());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public void deleteCourse(AccessToken token, @PathVariable Long id) {
        logger.info("deleting course with id: {}", id);
        courseService.deleteCourse(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public Course editCourse(AccessToken token, @PathVariable Long id, @RequestBody Course newCourse) {
        logger.info("editing course with id: {}", id);
        return courseService.updateCourse(id, newCourse);
    }

    @GetMapping("/{id}/activities")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<Activity> getCourseActivityList(AccessToken token, @PathVariable Long id) {
        logger.info("activity list from course with id: {}", id);
        return courseService.courseActivityList(id);
    }

    @PostMapping("/{courseId}/students")
    @PreAuthorize("hasRole('TEACHER')")
    public Course addStudentToCourse(AccessToken token, @PathVariable Long courseId, @RequestBody User student) {
        logger.info("adding student with email: {} to course with id: {}", student.getEmail(), courseId);
        return courseService.addStudent(courseId, student.getEmail());
    }

    @DeleteMapping("/{courseId}/students")
    @PreAuthorize("hasRole('TEACHER')")
    public Course removeStudentFromCourse(AccessToken token, @PathVariable Long courseId, @RequestBody User student) {
        logger.info("removing student with email: {} from course with id: {}", student.getEmail(), courseId);
        return courseService.deleteStudent(courseId, student.getEmail());
    }

}
