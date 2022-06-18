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
}
