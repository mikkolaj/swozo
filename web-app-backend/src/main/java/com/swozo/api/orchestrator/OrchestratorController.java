package com.swozo.api.orchestrator;

import com.swozo.security.rules.secret.SecretKeyAuthentication;
import com.swozo.utils.ServiceType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrchestratorController {

    @GetMapping("/orchestrator-test")
    public void test(SecretKeyAuthentication<ServiceType> authentication) {
        System.out.println(authentication);
        System.out.println(authentication.getPrincipal());
    }
}
