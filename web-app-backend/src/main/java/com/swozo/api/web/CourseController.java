package com.swozo.api.web;

import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.Course;
import com.swozo.security.AccessToken;
import com.swozo.webservice.service.CourseService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.LinkedList;

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
        return new Course();
    }

//    przyjmujemy json jako jakies parametry utworzenia?
    @PostMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public String addCourse(AccessToken token, @RequestBody Course course) {
        System.out.println("creating new course");
        return "course_id";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteCourse(AccessToken token, @PathVariable long id) {
        System.out.println("deleting course with id: " + id);
        return "course deleted";
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String editCourse(AccessToken token, @PathVariable long id, @RequestBody Course newCourse) {
        System.out.println("edititng course");
        return "course updated";
    }

    @GetMapping("/{id}/activities")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<Activity> getCourseActivityList(AccessToken token, @PathVariable long id) {
        System.out.println("activity list from course with id: " + id);
        return new LinkedList<>();
    }

    @PostMapping("/{courseId}/students/{studentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public String addStudentToCourse(AccessToken token, @PathVariable long courseId, @PathVariable long studentId) {
        System.out.println("adding student with id: " + studentId + " to course with id: " + courseId);
        return "student added";
    }

    @DeleteMapping("/{courseId}/students/{studentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public String removeStudentFromCourse(AccessToken token, @PathVariable long courseId, @PathVariable long studentId) {
        System.out.println("removing student with id: " + studentId + " to course with id: " + courseId);
        return "student removed";
    }

}