package com.swozo.orchestrator.api.scheduling.control.helpers;

import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceDescriptionEntity;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisionerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ScheduleRequestFilter {
    private final TimedSoftwareProvisionerFactory provisionerFactory;

    public boolean endsBeforeAvailability(ScheduleRequestWithServiceDescription scheduleData) {
        return scheduleData.request().getEndTime()
                .isBefore(getTargetAvailability(scheduleData.description()));
    }

    public boolean endsAfterAvailability(ScheduleRequestWithServiceDescription scheduleData) {
        return scheduleData.request().getEndTime()
                .isAfter(getTargetAvailability(scheduleData.description()));
    }

    private LocalDateTime getTargetAvailability(ServiceDescriptionEntity serviceDescription) {
        var provisioningSeconds = provisionerFactory
                .getProvisioner(serviceDescription.getServiceType())
                .getProvisioningSeconds(serviceDescription.getDynamicProperties());

        return LocalDateTime.now().plusSeconds(provisioningSeconds);
    }

}
