package com.swozo.api.web.user;

import com.swozo.api.web.auth.AuthService;
import com.swozo.api.web.auth.dto.RefreshTokenDto;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.user.dto.UserAdminDetailsDto;
import com.swozo.api.web.user.dto.UserAdminSummaryDto;
import com.swozo.api.web.user.dto.UserDetailsDto;
import com.swozo.api.web.user.request.CreateUserRequest;
import com.swozo.security.AccessToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping("/users")
@SecurityRequirement(name = ACCESS_TOKEN)
@RequiredArgsConstructor
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final UserAdminService userAdminService;
    private final AuthService authService;

    @GetMapping("/me")
    public UserDetailsDto getUserInfo(AccessToken token) {
        var userId = token.getUserId();
        logger.info("user info for user with id: {}", userId);
        return userService.getUserInfo(userId);
    }

    @GetMapping("/admins")
    public List<UserDetailsDto> getSystemAdmins() {
        return userService.getSystemAdmins();
    }

    @PostMapping("/me/logout")
    public void logout(AccessToken accessToken, @RequestBody RefreshTokenDto refreshTokenDto) {
        authService.logout(refreshTokenDto, accessToken.getUserId());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserAdminDetailsDto createUser(AccessToken accessToken, @RequestBody CreateUserRequest request) {
        logger.info("user creation request issued by: {} for user: {}", accessToken.getUserId(), request);
        return userAdminService.createUser(request);
    }

    @GetMapping("/details/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserAdminDetailsDto getUserDetailsForAdmin(@PathVariable Long userId) {
        logger.info("user details for user with id: {}", userId);
        return userAdminService.getUserDetailsForAdmin(userId);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserAdminSummaryDto> getUsers() {
        return userAdminService.getUsersForAdmin();
    }

    @PutMapping("/roles/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserAdminDetailsDto setUserRoles(AccessToken accessToken, @PathVariable Long userId, @RequestBody List<RoleDto> roles) {
        logger.info("user {} is assigning roles: {} for user with id: {}", accessToken.getUserId(), roles, userId);
        return userAdminService.setUserRoles(userId, roles);
    }
}
