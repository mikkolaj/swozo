package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.ScheduleResponse;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.model.scheduling.properties.ServiceType;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ServiceTypeMapper;
import com.swozo.orchestrator.cloud.software.InvalidParametersException;
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
    private final ServiceTypeMapper serviceTypeMapper;

    public ScheduleResponse schedule(ScheduleRequest request) {
        try {
            var response = scheduleHandler.startTracking(request);
//            scheduleHandler.delegateScheduling(response);
            return new ScheduleResponse(response.getId());
        } catch (IllegalArgumentException | InvalidParametersException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    public List<ServiceConfig> getSupportedServices() {
        return provisionerFactory.getAllProvisioners()
                .stream()
                .map(TimedSoftwareProvisioner::getServiceConfig)
                .toList();
    }

    public ServiceConfig getServiceConfig(ServiceType serviceType) {
        return provisionerFactory.getProvisioner(serviceTypeMapper.toPersistence(serviceType)).getServiceConfig();
    }
}
