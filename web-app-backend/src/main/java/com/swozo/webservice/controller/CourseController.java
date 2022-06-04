package com.swozo.webservice.controller;

import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.Course;
import com.swozo.databasemodel.users.User;
import com.swozo.security.AccessToken;
import com.swozo.webservice.service.CourseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.LinkedList;

import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping("/course")
@SecurityRequirement(name = ACCESS_TOKEN)
public class CourseController {
    @Autowired
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    //    tu nwm do czego to jeszcze będzie służyć, ale pewnie sie przyda
    @GetMapping("/course_info_by_id")
    @PreAuthorize("hasRole('TEACHER')")
    public Course getCourseJson(AccessToken token) {
        System.out.println("course  info getter");
        return new Course();
    }

    @GetMapping("/activity_list")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<Activity> getActivityList(AccessToken token) {
        System.out.println("activity list");
        return new LinkedList<>();
    }

    @PostMapping("/add_activity")
    @PreAuthorize("hasRole('TEACHER')")
    public String addActivityToCourse(AccessToken token) {
        System.out.println("creating new activity inside course");
        return "activity_id";
    }

    @DeleteMapping("/delete_activity")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteActivityFromCourse(AccessToken token) {
        System.out.println("deleting activity from course");
        return "activity deleted";
    }


    @GetMapping("/student_list")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<User> getStudentList(AccessToken token) {
        System.out.println("students list");
        return new LinkedList<>();
    }

    @GetMapping("/student_list_inside_course")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<User> getStudentListFromCourse(AccessToken token) {
        System.out.println("students list inside course");
        return new LinkedList<>();
    }

    @PostMapping("/add_student")
    @PreAuthorize("hasRole('TEACHER')")
    public String addStudentToCourse(AccessToken token) {
        System.out.println("creating new activity inside course");
        return "student added";
    }

    @DeleteMapping("/remove_student")
    @PreAuthorize("hasRole('TEACHER')")
    public String removeStudentFromCourse(AccessToken token) {
        System.out.println("removing student from course");
        return "student removed";
    }

}
