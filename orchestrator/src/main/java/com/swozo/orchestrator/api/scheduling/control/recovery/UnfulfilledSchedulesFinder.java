package com.swozo.orchestrator.api.scheduling.control.recovery;

import com.swozo.orchestrator.api.scheduling.control.ScheduleRequestInitializer;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus;
import com.swozo.orchestrator.api.scheduling.persistence.repository.ScheduleRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class UnfulfilledSchedulesFinder {
    private final ScheduleRequestRepository requestRepository;
    private final ScheduleRequestInitializer initializer;

    public List<ScheduleRequestEntity> getSchedulesToDelete() {
        var provisioned = requestRepository
                .findScheduleRequestsWithAllServiceDescriptionsInStatus(
                        ServiceStatus.asStrings(ServiceStatus.readyToBeDeleted())
                );

        return initializer.initializeParameters(provisioned);
    }

    public List<ScheduleRequestEntity> getOutdatedSchedulesWithoutVm() {
        return requestRepository
                .findScheduleRequestsWithAllServiceDescriptionsInStatus(
                        ServiceStatus.asStrings(ServiceStatus.withoutVm())
                ).stream()
                .filter(requestEntity -> requestEntity.getEndTime().isBefore(LocalDateTime.now()))
                .toList();
    }

    public List<ScheduleRequestEntity> getValidSchedulesToRestartFromBeginning() {
        var schedulesToRestart = getValidSchedulesWithStatusIn(ServiceStatus.withoutVm());

        return initializer.initializeParameters(schedulesToRestart);
    }

    public List<ScheduleRequestEntity> getSchedulesWithVmBeforeExport() {
        var provisioned = new HashSet<>(requestRepository
                .findByServiceDescriptions_StatusIn(ServiceStatus.withVmBeforeExport())).stream().toList();

        return initializer.initializeParameters(provisioned);
    }

    private List<ScheduleRequestEntity> getValidSchedulesWithStatusIn(Set<ServiceStatus> statuses) {
        return requestRepository.findByEndTimeGreaterThanAndServiceDescriptions_StatusIn(LocalDateTime.now(), statuses)
                .stream()
                .toList();
    }
}
