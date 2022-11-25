package com.swozo.orchestrator.api.scheduling.control.helpers;

import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus;
import com.swozo.orchestrator.api.scheduling.persistence.repository.ScheduleRequestRepository;
import com.swozo.orchestrator.api.scheduling.persistence.repository.ServiceDescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AbortHandler {
    private final ScheduleRequestRepository requestRepository;
    private final ServiceDescriptionRepository descriptionRepository;

    public void abortIfNecessary(long serviceDescriptionId) throws CancelledScheduleException {
        if (ServiceStatus.toBeAborted().contains(descriptionRepository.getById(serviceDescriptionId).getStatus())) {
            throw new CancelledScheduleException(String.format("Service with [id: %s] has been cancelled.", serviceDescriptionId));
        }
    }

    public void abortRequestIfNecessary(long scheduleRequestId) throws CancelledScheduleException {
        if (wasCancelled(scheduleRequestId)) {
            throw new CancelledScheduleException(String.format("Schedule with [id: %s] has been cancelled.", scheduleRequestId));
        }
    }

    private boolean wasCancelled(long scheduleRequestId) {
        return requestRepository.getById(scheduleRequestId)
                .getServiceDescriptions()
                .stream()
                .anyMatch(description -> ServiceStatus.toBeAborted().contains(description.getStatus()));
    }
}
