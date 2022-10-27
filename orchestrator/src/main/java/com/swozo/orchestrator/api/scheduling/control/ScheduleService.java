package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.ScheduleResponse;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisionerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final TimedSoftwareProvisionerFactory provisionerFactory;
    private final ScheduleHandler scheduleHandler;

    public ScheduleResponse schedule(ScheduleRequest request) {
        var response = scheduleHandler.startTracking(request);
        scheduleHandler.delegateScheduling(response.entity(), response.provisioner());
        return new ScheduleResponse(response.entity().getId());
    }

    public List<ServiceConfig> getSupportedServices() {
        return provisionerFactory.getAllProvisioners()
                .stream()
                .map(TimedSoftwareProvisioner::getServiceConfig)
                .toList();
    }
}
