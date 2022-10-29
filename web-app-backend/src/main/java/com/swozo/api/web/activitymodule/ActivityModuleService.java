package com.swozo.api.web.activitymodule;

import com.swozo.mapper.ActivityModuleMapper;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.persistence.activity.ActivityModule;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityModuleService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ActivityModuleRepository activityModuleRepository;
    private final ActivityModuleMapper activityModuleMapper;

    public void setActivityLinks(Long requestId, List<ActivityLinkInfo> links) {
        var activityModule = activityModuleRepository.findByRequestId(requestId).orElseThrow();
        addLinksToActivityModule(activityModule, links);
        activityModuleRepository.save(activityModule);
    }

    private void addLinksToActivityModule(ActivityModule activityModule, List<ActivityLinkInfo> links) {
        links.stream()
            .map(activityModuleMapper::toPersistence)
            .forEach(activityModule::addLink);
    }
}
