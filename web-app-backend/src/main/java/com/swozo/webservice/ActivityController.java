package com.swozo.webservice;

import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.Course;
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
@RequestMapping("/activity")
@SecurityRequirement(name = ACCESS_TOKEN)
public class ActivityController {
    private final String activityService = "course service";

    @GetMapping("/activity_info_by_id")
    @PreAuthorize("hasRole('TEACHER')")
    public Course getCourseJson(AccessToken token){
        System.out.println("course  info getter");
        return new Course();
    }

    @GetMapping("/module_list")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<Module> getModuleList(AccessToken token){
        System.out.println("module list");
        return new LinkedList<Module>();
    }

    @PostMapping("/add_module_to_activity")
    @PreAuthorize("hasRole('TEACHER')")
    public String addModuleToActivity(AccessToken token){
        System.out.println("adding module to activity");
        return "activity_id";
    }

    @PostMapping("/delete_module_from_activity")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteModuleFromActivity(AccessToken token){
        System.out.println("deleting module from activity");
        return "activity_id";
    }

//    pytanie czy jaką tu dać autroyzacje? bo czy jak więcej ról moze mieć
//    dostęp to mam wpisywać więcej PreAuthorize?
    @GetMapping("/links")
    @PreAuthorize("hasRole('TEACHER')")
    public String getLinks(AccessToken token){
        System.out.println("sending links");
        return "links";
    }

}
