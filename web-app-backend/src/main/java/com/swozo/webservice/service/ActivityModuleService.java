package com.swozo.webservice.service;

import com.swozo.api.orchestratorclient.OrchestratorService;
import com.swozo.databasemodel.ActivityModule;
import com.swozo.dto.activitymodule.ActivityModuleDetailsResp;
import com.swozo.mapper.dto.ActivityModuleMapper;
import com.swozo.model.links.OrchestratorLinkResponse;
import com.swozo.webservice.exceptions.ActivityModuleNotFoundException;
import com.swozo.webservice.repository.ActivityModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class ActivityModuleService {
    private final ActivityModuleRepository activityModuleRepository;
    private final ActivityModuleMapper activityModuleMapper;
    private final OrchestratorService orchestratorService;

    public ActivityModule getActivityModule(Long activityModuleId) {
        return activityModuleRepository.findById(activityModuleId)
                .orElseThrow(() -> new ActivityModuleNotFoundException(activityModuleId));
    }

    public Collection<ActivityModuleDetailsResp> getActivityModuleList() {
        return new LinkedList<>();
    }

    public ActivityModuleDetailsResp getActivityModuleInfo(Long activityModuleId) {
        ActivityModule activityModule = getActivityModule(activityModuleId);
        return activityModuleMapper.toModel(activityModule);
    }

    public void provideLinksForActivityModules(Collection<ActivityModule> activityModules) {
        activityModules.stream()
                .filter(activityModule -> activityModule.getLinks().isEmpty())
                .forEach(activityModule -> {
                    Long activityModuleId = activityModule.getId();
                    OrchestratorLinkResponse orchestratorLinkResponse =
                            orchestratorService.getActivityLinks(activityModuleId);
                    System.out.println("GOT links: " + orchestratorLinkResponse.links());
                    orchestratorLinkResponse
                            .links()
                            .forEach(activityModule::addLink);
                    activityModuleRepository.save(activityModule);
                });
    }

}
