package com.swozo.orchestrator.api.links;

import com.swozo.config.Config;
import com.swozo.model.links.OrchestratorLinkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Config.LINKS)
public class LinkController {
    private final LinkService service;

    @Autowired
    public LinkController(LinkService service) {
        this.service = service;
    }

    @GetMapping("/{scheduleRequestId}")
    public OrchestratorLinkResponse getLinks(@PathVariable Long scheduleRequestId) {
        return new OrchestratorLinkResponse(scheduleRequestId, service.getLinks(scheduleRequestId));
    }
}
