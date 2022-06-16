package com.swozo.api.web;

import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.Course;
import com.swozo.security.AccessToken;
import com.swozo.webservice.service.CourseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    @GetMapping("/all-courses/{teacherId}")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<Course> getActivityList(AccessToken token, @PathVariable Long teacherId) {
        logger.info("course list for teacher with id: " + teacherId);
        return courseService.getCoursesForTeacher(teacherId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public Course getCourse(AccessToken token, @PathVariable Long id) {
        logger.info("course info getter for id: " + id);
        return courseService.getCourse(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('TEACHER')")
    public Course addCourse(AccessToken token, @RequestBody Course course) {
        logger.info("creating new course");
        return courseService.createCourse(course);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteCourse(AccessToken token, @PathVariable Long id) {
        logger.info("deleting course with id: " + id);
        courseService.deleteCourse(id);
        return "course deleted";
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public Course editCourse(AccessToken token, @PathVariable Long id, @RequestBody Course newCourse) {
        logger.info("editing course with id: " + id);
        return courseService.updateCourse(id, newCourse);
    }

    @GetMapping("/{id}/activities")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<Activity> getCourseActivityList(AccessToken token, @PathVariable Long id) {
        logger.info("activity list from course with id: " + id);
        return courseService.courseActivityList(id);
    }

    @PostMapping("/{courseId}/students/{studentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public Course addStudentToCourse(AccessToken token, @PathVariable Long courseId, @PathVariable Long studentId) {
        logger.info("adding student with id: " + studentId + " to course with id: " + courseId);
        return courseService.addSudent(courseId, studentId);
    }

    @DeleteMapping("/{courseId}/students/{studentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public Course removeStudentFromCourse(AccessToken token, @PathVariable Long courseId, @PathVariable Long studentId) {
        logger.info("removing student with id: " + studentId + " to course with id: " + courseId);
        return courseService.deleteStudent(courseId, studentId);
    }

}
