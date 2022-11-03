package com.swozo.api.web.mda.policy;

import com.swozo.api.web.mda.policy.dto.PolicyDto;
import com.swozo.api.web.mda.policy.request.CreatePolicyRequest;
import com.swozo.api.web.mda.policy.request.EditPolicyRequest;
import com.swozo.security.AccessToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping("/policies")
@SecurityRequirement(name = ACCESS_TOKEN)
@RequiredArgsConstructor
public class PolicyController {
    private final Logger logger = LoggerFactory.getLogger(PolicyController.class);
    private final PolicyService policyService;

    @GetMapping("/all-system-policies")
    @PreAuthorize("hasRole('ADMIN')")
    public Collection<PolicyDto> allSystemPolicies(AccessToken token) {
        logger.info("getting all policies");
        return policyService.getAllSystemPolicies();
    }

    @GetMapping("/all_teacher-policies/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Collection<PolicyDto> allTeacherPolicies(AccessToken tokenm, @PathVariable Long teacherId){
        logger.info("policies for userId: {}", teacherId);
        return policyService.getAllTeacherPoliciesDto(teacherId);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public PolicyDto addPolicy(AccessToken token, @RequestBody CreatePolicyRequest createPolicyRequest) {
        logger.info("creating new policy for userId: {}", createPolicyRequest.teacherId());
        return policyService.createPolicy(createPolicyRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePolicy(AccessToken token, @PathVariable Long id) {
        logger.info("deleting policy with id: {}", id);
        policyService.deletePolicy(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PolicyDto editPolicy(AccessToken token, @PathVariable Long id, @RequestBody EditPolicyRequest editPolicyRequest) {
        logger.info("editing policy with id: {}", id);
        return policyService.editPolicy(id, editPolicyRequest);
    }


}
