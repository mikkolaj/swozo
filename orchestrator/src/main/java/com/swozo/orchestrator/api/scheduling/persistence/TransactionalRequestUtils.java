package com.swozo.orchestrator.api.scheduling.persistence;

import com.swozo.model.scheduling.properties.MdaVmSpecs;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ScheduleRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class TransactionalRequestUtils {
    private final ScheduleRequestMapper requestMapper;

    public MdaVmSpecs toMdaVmSpecs(ScheduleRequestEntity requestEntity) {
        return requestMapper.toMdaVmSpecs(requestEntity);
    }

}
