package com.swozo.webservice.controller;

import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.Course;
import com.swozo.databasemodel.users.User;
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
@RequestMapping("/students")
@SecurityRequirement(name = ACCESS_TOKEN)
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StudentController {

    @GetMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<User> getStudentList(AccessToken token) {
        System.out.println("students list");
        return new LinkedList<>();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<User> getStudentListFromCourse(AccessToken token, @PathVariable long id) {
        System.out.println("students list inside course with id: " + id);
        return new LinkedList<>();
    }
}
