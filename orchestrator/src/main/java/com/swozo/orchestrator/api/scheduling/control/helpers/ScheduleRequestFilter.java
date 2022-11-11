package com.swozo.orchestrator.api.scheduling.control.helpers;

import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceTypeEntity;
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
                .isBefore(getTargetAvailability(scheduleData.description().getServiceType()));
    }

    public boolean endsAfterAvailability(ScheduleRequestWithServiceDescription scheduleData) {
        return scheduleData.request().getEndTime()
                .isAfter(getTargetAvailability(scheduleData.description().getServiceType()));
    }


    private LocalDateTime getTargetAvailability(ServiceTypeEntity serviceType) {
        var provisioningSeconds = provisionerFactory.getProvisioner(serviceType).getProvisioningSeconds();
        return LocalDateTime.now().plusSeconds(provisioningSeconds);
    }


}
