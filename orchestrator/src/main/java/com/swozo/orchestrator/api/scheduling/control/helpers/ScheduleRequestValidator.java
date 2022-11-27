package com.swozo.orchestrator.api.scheduling.control.helpers;

import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ServiceTypeMapper;
import com.swozo.orchestrator.cloud.software.InvalidParametersException;
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
        var earliestReadyTime = request.serviceDescriptions().stream()
                .map(description -> {
                    var serviceTypeEntity = serviceTypeMapper.toPersistence(description.serviceType());
                    var provisioner = provisionerFactory.getProvisioner(serviceTypeEntity);
                    provisioner.validateParameters(description.dynamicProperties());
                    return provisioner.getProvisioningSeconds(description.dynamicProperties());
                })
                .reduce(Integer::sum)
                .map(totalProvisioningTimeSeconds -> LocalDateTime.now().plusSeconds(totalProvisioningTimeSeconds))
                .orElseThrow(() -> new IllegalArgumentException("Can't schedule a request with no service descriptions"));

//        if (request.serviceLifespan().endTime().isBefore(earliestReadyTime)) {
//            throw new IllegalArgumentException("End time is before earliest possible ready time.");
//        }
    }
}
