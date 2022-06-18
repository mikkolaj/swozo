package com.swozo.api.web;

import com.swozo.dto.user.UserDetailsResp;
import com.swozo.security.AccessToken;
import com.swozo.webservice.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping("/users")
@SecurityRequirement(name = ACCESS_TOKEN)
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

//    we may use in the future getmapping without any path to get all users for admin...
    @GetMapping("/me")
    public UserDetailsResp getUserInfo(AccessToken token) {
        Long userId = token.getUserId();
        logger.info("user info for user with id: {}", userId);
        return userService.getUserInfo(userId);
    }

}
