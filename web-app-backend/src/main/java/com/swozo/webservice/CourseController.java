package com.swozo.webservice;

import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.Course;
import com.swozo.databasemodel.users.User;
import com.swozo.security.AccessToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.LinkedList;

import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping("/course")
@SecurityRequirement(name = ACCESS_TOKEN)
public class CourseController {
    private final String courseService = "course service";

//    tu nwm do czego to jeszcze będzie służyć, ale pewnie sie przyda
    @GetMapping("/course_info_by_id")
    @PreAuthorize("hasRole('TEACHER')")
    public Course getCourseJson(AccessToken token){
        System.out.println("course  info getter");
        return new Course();
    }

    @GetMapping("/activity_list")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<Activity> getActivityList(AccessToken token){
        System.out.println("activity list");
        return new LinkedList<Activity>();
    }

    @PostMapping("/add_activity")
    @PreAuthorize("hasRole('TEACHER')")
    public String addActivityToCourse(AccessToken token){
        System.out.println("creating new activity inside course");
        return "activity_id";
    }

    @PostMapping("/delete_activiy")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteActivityFromCourse(AccessToken token){
        System.out.println("deleting activity from course");
        return "activity deleted";
    }


    @GetMapping("/student_list")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<User> getStudentList(AccessToken token){
        System.out.println("students list");
        return new LinkedList<User>();
    }

    @GetMapping("/student_list_inside_course")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<User> getStudentListFromCourse(AccessToken token){
        System.out.println("students list inside course");
        return new LinkedList<User>();
    }

    @PostMapping("/add_student")
    @PreAuthorize("hasRole('TEACHER')")
    public String addStudentToCourse(AccessToken token){
        System.out.println("creating new activity inside course");
        return "student added";
    }

    @PostMapping("/remove_student")
    @PreAuthorize("hasRole('TEACHER')")
    public String removeStudentFromCourse(AccessToken token){
        System.out.println("removing student from course");
        return "student removed";
    }







}
