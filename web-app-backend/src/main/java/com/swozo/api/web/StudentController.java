package com.swozo.api.web;

import com.swozo.databasemodel.User;
import com.swozo.security.AccessToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.LinkedList;

import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping("/students")
@SecurityRequirement(name = ACCESS_TOKEN)
public class StudentController {

    @GetMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<User> getStudentList(AccessToken token) {
        System.out.println("students list");
        return new LinkedList<>();
    }

    @GetMapping("/{courseId}")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<User> getStudentListFromCourse(AccessToken token, @PathVariable long courseId) {
        System.out.println("students list inside course with id: " + courseId);
        return new LinkedList<>();
    }
}
