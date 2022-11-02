package com.swozo.api.web.auth;

import com.swozo.api.web.auth.dto.AuthDetailsDto;
import com.swozo.api.web.auth.dto.RefreshTokenDto;
import com.swozo.api.web.auth.request.LoginRequest;
import com.swozo.api.web.auth.request.ResetPasswordRequest;
import com.swozo.api.web.auth.request.SendResetPasswordEmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public AuthDetailsDto login(@RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    @PostMapping("/refresh")
    public AuthDetailsDto refreshAccessToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        return authService.refreshAccessToken(refreshTokenDto);
    }

    @PostMapping("/reset-password")
    public void sendResetPasswordEmail(@RequestBody SendResetPasswordEmailRequest request) {
        authService.sendResetPasswordEmail(request.email());
    }

    @PutMapping("/reset-password")
    public void resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
    }

}
