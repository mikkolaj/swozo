package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ServiceTypeMapper;
import com.swozo.orchestrator.cloud.software.InvalidParametersException;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisionerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScheduleRequestValidator {

    private final TimedSoftwareProvisionerFactory provisionerFactory;
    private final ServiceTypeMapper serviceTypeMapper;

    public void validate(ScheduleRequest request) throws IllegalArgumentException, InvalidParametersException {
        if (request.serviceDescriptions().isEmpty()) {
            throw new IllegalArgumentException("Can't schedule a request with no service descriptions");
        }

        request.serviceDescriptions().forEach(description -> {
            var serviceTypeEntity = serviceTypeMapper.toPersistence(description.serviceType());
            var provisioner = provisionerFactory.getProvisioner(serviceTypeEntity);
            checkTimeBounds(request, provisioner);
            provisioner.validateParameters(description.dynamicProperties());
        });

    }

    private void checkTimeBounds(ScheduleRequest request, TimedSoftwareProvisioner provisioner) {
        if (isTooLate(request, provisioner)) {
            throw new IllegalArgumentException("End time is before earliest possible ready time.");
        } else if (lastsNotLongEnough(request, provisioner)) {
            throw new IllegalArgumentException("End time is before estimated ready time.");
        }
    }

    private boolean isTooLate(ScheduleRequest request, TimedSoftwareProvisioner provisioner) {
        var earliestReadyTime = LocalDateTime.now().plusSeconds(provisioner.getProvisioningSeconds());
        return request.serviceLifespan().endTime()
                .isBefore(earliestReadyTime);
    }

    private boolean lastsNotLongEnough(ScheduleRequest request, TimedSoftwareProvisioner provisioner) {
        var estimatedReadyTime =
                request.serviceLifespan().startTime().plusSeconds(provisioner.getProvisioningSeconds());
        return request.serviceLifespan().endTime()
                .isBefore(estimatedReadyTime);
    }
}
