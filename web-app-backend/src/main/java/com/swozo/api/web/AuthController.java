package com.swozo.api.web;

import com.swozo.dto.auth.AuthDetailsDto;
import com.swozo.dto.auth.LoginRequest;
import com.swozo.webservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Login user")
    @PostMapping("/login")
    public AuthDetailsDto login(@RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }
}
