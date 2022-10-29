package com.swozo.api.web.sandbox;

import com.swozo.api.web.sandbox.dto.ServiceModuleSandboxDto;
import com.swozo.api.web.sandbox.request.CreateSandboxEnvironmentRequest;
import com.swozo.security.AccessToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping("/sandbox")
@SecurityRequirement(name = ACCESS_TOKEN)
@RequiredArgsConstructor
public class SandboxController {
    private final SandboxService sandboxService;

    @PostMapping("/{serviceModuleId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'TECHNICAL_TEACHER')")
    public ServiceModuleSandboxDto createServiceModuleTestingEnvironment(
            AccessToken accessToken,
            @PathVariable Long serviceModuleId,
            @RequestBody CreateSandboxEnvironmentRequest request
    ) {
        return sandboxService.createServiceModuleTestingEnvironment(accessToken.getUserId(), serviceModuleId, request);
    }
}
