package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.ScheduleResponse;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.model.scheduling.properties.ScheduleType;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisionerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final TimedSoftwareProvisionerFactory provisionerFactory;
    private final ScheduleHandler scheduleHandler;

    public ScheduleResponse schedule(ScheduleRequest request) {
        try {
            var response = scheduleHandler.startTracking(request);
            scheduleHandler.delegateScheduling(response.entity(), response.provisioner());
            return new ScheduleResponse(response.entity().getId());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    public List<ServiceConfig> getSupportedServices() {
        return provisionerFactory.getAllProvisioners()
                .stream()
                .map(TimedSoftwareProvisioner::getServiceConfig)
                .toList();
    }

    public ServiceConfig getServiceConfig(ScheduleType scheduleType) {
        return provisionerFactory.getProvisioner(scheduleType).getServiceConfig();
    }
}
