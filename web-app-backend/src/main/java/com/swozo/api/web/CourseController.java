package com.swozo.api.web;

import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.Course;
import com.swozo.security.AccessToken;
import com.swozo.webservice.service.CourseService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public Course getCourse(AccessToken token, @PathVariable int id) {
        System.out.println("course info getter for id: " + id);
        Course course = courseService.getCourse(id);
        System.out.println("kurs3: " + course);
        return course;
    }

//    przyjmujemy json jako jakies parametry utworzenia?
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('TEACHER')")
    public Course addCourse(AccessToken token, @RequestBody Course course) {
        System.out.println("creating new course");
        return courseService.createCourse(course);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteCourse(AccessToken token, @PathVariable long id) {
        System.out.println("deleting course with id: " + id);
        courseService.deleteCourse(id);
        return "course deleted";
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public Course editCourse(AccessToken token, @PathVariable long id, @RequestBody Course newCourse) {
        System.out.println("edititng course");
        return courseService.updateCourse(id, newCourse);
    }

    @GetMapping("/{id}/activities")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<Activity> getCourseActivityList(AccessToken token, @PathVariable long id) {
        System.out.println("activity list from course with id: " + id);
        return courseService.courseActivityList(id);
    }

    @PostMapping("/{courseId}/students/{studentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public Course addStudentToCourse(AccessToken token, @PathVariable long courseId, @PathVariable long studentId) {
        System.out.println("adding student with id: " + studentId + " to course with id: " + courseId);
        return courseService.addSudent(courseId,studentId);
    }

    @DeleteMapping("/{courseId}/students/{studentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public Course removeStudentFromCourse(AccessToken token, @PathVariable long courseId, @PathVariable long studentId) {
        System.out.println("removing student with id: " + studentId + " to course with id: " + courseId);
        return courseService.deleteStudent(courseId, studentId);
    }

}
