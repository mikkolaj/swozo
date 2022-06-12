package com.swozo.orchestrator.api.links;

import com.swozo.model.links.OrchestratorLinkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/links")
public class LinkController {
    private final LinkService service;

    @Autowired
    public LinkController(LinkService service) {
        this.service = service;
    }

    @GetMapping("/{activityModuleId}")
    public OrchestratorLinkResponse getLinks(@PathVariable Long activityModuleId) {
        return new OrchestratorLinkResponse(activityModuleId, service.getLinks(activityModuleId));
    }
}
