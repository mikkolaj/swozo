package com.swozo.webservice.service;

import com.swozo.api.orchestratorclient.OrchestratorService;
import com.swozo.databasemodel.ActivityModule;
import com.swozo.dto.activitymodule.ActivityModuleDetailsDto;
import com.swozo.mapper.ActivityModuleMapper;
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

    public Collection<ActivityModuleDetailsDto> getActivityModuleList() {
        return new LinkedList<>();
    }

    public ActivityModuleDetailsDto getActivityModuleInfo(Long activityModuleId) {
        ActivityModule activityModule = getActivityModule(activityModuleId);
        return activityModuleMapper.toDto(activityModule);
    }

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
