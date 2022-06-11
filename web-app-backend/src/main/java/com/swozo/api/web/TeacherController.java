package com.swozo.api.web;

import com.swozo.databasemodel.Course;
import com.swozo.security.AccessToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.LinkedList;

import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping("/teachers")
@SecurityRequirement(name = ACCESS_TOKEN)
public class TeacherController {
    private final String teacherService = "course service";

    @GetMapping("/{id}/course-list")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<Course> getActivityList(AccessToken token, @PathVariable long id) {
        System.out.println("course list for teacher with id: " + id);
        return new LinkedList<>();
    }
}