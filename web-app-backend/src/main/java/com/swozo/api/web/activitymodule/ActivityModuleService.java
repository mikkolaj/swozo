package com.swozo.api.web.activitymodule;

import com.swozo.api.orchestrator.OrchestratorService;
import com.swozo.mapper.ActivityModuleMapper;
import com.swozo.model.links.OrchestratorLinkResponse;
import com.swozo.persistence.activity.ActivityModule;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ActivityModuleService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ActivityModuleRepository activityModuleRepository;
    private final ActivityModuleMapper activityModuleMapper;
    private final OrchestratorService orchestratorService;

    public void provideLinksForActivityModules(Collection<ActivityModule> activityModules) {
        activityModules.stream()
                .filter(activityModule -> activityModule.getLinks().isEmpty())
                .filter(activityModule -> activityModule.getRequestId().isPresent())
                .forEach(activityModule -> {
                    try {
                        var linksResp = orchestratorService.getActivityLinks(activityModule.getRequestId().get());
                        addLinksToActivityModule(activityModule, linksResp);
                        activityModuleRepository.save(activityModule);
                    } catch (Exception ex) {
                        logger.error("Failed to fetch activity links for serviceModule: " + activityModule, ex);
                    }
                });
    }

    private void addLinksToActivityModule(ActivityModule activityModule, OrchestratorLinkResponse response) {
        response.links().stream()
                .map(activityModuleMapper::toPersistence)
                .forEach(activityModule::addLink);
    }
}
