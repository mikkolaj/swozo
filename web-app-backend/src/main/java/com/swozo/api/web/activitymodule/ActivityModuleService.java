package com.swozo.api.web.activitymodule;

import com.swozo.api.orchestrator.OrchestratorService;
import com.swozo.mapper.ActivityModuleMapper;
import com.swozo.persistence.ActivityModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ActivityModuleService {
    private final ActivityModuleRepository activityModuleRepository;
    private final ActivityModuleMapper activityModuleMapper;
    private final OrchestratorService orchestratorService;

    public void provideLinksForActivityModules(Collection<ActivityModule> activityModules) {
        activityModules.stream()
                .filter(activityModule -> activityModule.getLinks().isEmpty())
                .forEach(activityModule -> {
                    orchestratorService.getActivityLinks(activityModule.getId()).links().stream()
                            .map(activityModuleMapper::toPersistence)
                            .forEach(activityModule::addLink);

                    activityModuleRepository.save(activityModule);
                });
    }
}
