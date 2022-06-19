package com.swozo.webservice.service;

import com.swozo.databasemodel.ActivityModule;
import com.swozo.dto.activitymodule.ActivityModuleDetailsResp;
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
    ActivityModuleRepository activityModuleRepository;
    ActivityModuleMapper activityModuleMapper;

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


}
