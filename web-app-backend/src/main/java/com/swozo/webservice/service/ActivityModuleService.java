package com.swozo.webservice.service;

import com.swozo.api.orchestratorclient.OrchestratorService;
import com.swozo.databasemodel.ActivityModule;
import com.swozo.dto.activitymodule.ActivityModuleDetailsResp;
import com.swozo.mapper.dto.ActivityModuleMapper;
import com.swozo.model.links.Link;
import com.swozo.webservice.exceptions.ActivityModuleNotFoundException;
import com.swozo.webservice.repository.ActivityModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class ActivityModuleService {
    ActivityModuleRepository activityModuleRepository;
    ActivityModuleMapper activityModuleMapper;
    OrchestratorService orchestratorService;

    public ActivityModule getActivityModule(Long activityModuleId){
        return activityModuleRepository.findById(activityModuleId)
                .orElseThrow(() -> new ActivityModuleNotFoundException(activityModuleId));
    }

    public Collection<ActivityModuleDetailsResp> getActivityModuleList(){
        return new LinkedList<>();
    }

    public ActivityModuleDetailsResp getActivityModuleInfo(Long activityModuleId){
        ActivityModule activityModule = getActivityModule(activityModuleId);
        return activityModuleMapper.toModel(activityModule);
    }

    public void provideLinksForActivityModules(Collection<ActivityModule> activityModules){
        activityModules.forEach(activityModule -> {
            Long activityModuleId = activityModule.getId();
//            to use when orchestrator ready
//            OrchestratorLinkResponse orchestratorLinkResponse =
//                    orchestratorService.getActivityLinks(activityModuleId);
//            orchestratorLinkResponse.links().stream().map(Link::link)
//                    .forEach(activityModule::addLink);
            activityModule.addLink(new Link("link.pl", "instrukcja linkaaa"));
        });
    }

}
