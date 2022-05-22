package com.swozo.example;

import com.swozo.security.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example")
public class ExampleController {
    private final ExampleService exampleService;

    @Autowired
    public ExampleController(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    @GetMapping
    public String getExample() {
        return "Swozo";
    }

    // use @PreAuthorize with required roles to limit access to only these roles
    // put AccessToken in arg list to get token passed from client, token will contain userId
    @GetMapping("/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ExampleModel getExampleJson(AccessToken token) {
        System.out.println(token.getUserId() + "    " + token.getAuthorities());
        return exampleService.getExample();
    }
}
