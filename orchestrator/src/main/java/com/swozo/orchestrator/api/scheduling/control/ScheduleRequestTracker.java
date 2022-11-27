package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceDescriptionEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ScheduleRequestMapper;
import com.swozo.orchestrator.api.scheduling.persistence.repository.ScheduleRequestRepository;
import com.swozo.orchestrator.api.scheduling.persistence.repository.ServiceDescriptionRepository;
import com.swozo.orchestrator.cloud.resources.vm.TimedVmProvider;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisionerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleRequestTracker {
    private final ScheduleRequestRepository requestRepository;
    private final ServiceDescriptionRepository descriptionRepository;
    private final ScheduleRequestMapper requestMapper;
    private final ScheduleRequestInitializer initializer;
    private final TimedSoftwareProvisionerFactory provisionerFactory;
    private final TimedVmProvider vmProvider;

    public ScheduleRequestEntity startTracking(ScheduleRequest scheduleRequest) {
        var entity = requestRepository.save(requestMapper.toPersistence(scheduleRequest));
        initializer.initializeParameters(entity);
        return entity;
    }

    public ScheduleRequestEntity getScheduleRequest(long scheduleRequestId) {
        return initializer.initializeParameters(requestRepository.getById(scheduleRequestId));
    }

    public void updateStatus(ScheduleRequestEntity requestEntity, ServiceStatus status) {
        var currentRequestEntity = requestRepository.getById(requestEntity.getId());
        currentRequestEntity.getServiceDescriptions()
                .forEach(description -> setStatusIfTransitionIsValid(description, status));
        descriptionRepository.saveAll(currentRequestEntity.getServiceDescriptions());
    }

    public void updateStatus(ServiceDescriptionEntity descriptionEntity, ServiceStatus status) {
        var description = descriptionRepository.getById(descriptionEntity.getId());
        setStatusIfTransitionIsValid(description, status);
        descriptionRepository.save(description);
    }

    private void setStatusIfTransitionIsValid(ServiceDescriptionEntity serviceDescriptionEntity, ServiceStatus status) {
        if (serviceDescriptionEntity.getStatus().canTransitionTo(status)) {
            serviceDescriptionEntity.setStatus(status);
        } else {
            throw new IllegalStateException(String.format(
                    "Tried invalid state transition from %s to %s for serviceDescription [id: %s].",
                    serviceDescriptionEntity.getStatus(),
                    status,
                    serviceDescriptionEntity.getId()
            ));
        }
    }

    public boolean canBeImmediatelyDeleted(long scheduleRequestId) {
        return requestRepository.getById(scheduleRequestId).getServiceDescriptions().stream()
                .allMatch(description -> ServiceStatus.canBeImmediatelyDeleted().contains(description.getStatus()));
    }

    public boolean serviceWasCancelled(long serviceDescriptionId) {
        return descriptionRepository.getById(serviceDescriptionId)
                .getStatus() == ServiceStatus.CANCELLED;
    }

    public boolean serviceWasDeleted(long serviceDescriptionId) {
        return descriptionRepository.getById(serviceDescriptionId)
                .getStatus() == ServiceStatus.DELETED;
    }

    public boolean canBeProvisioned(long serviceDescriptionId) {
        return ServiceStatus.provisioning().contains(getServiceStatus(serviceDescriptionId));
    }

    public boolean shouldBeExported(ServiceDescriptionEntity description) {
        var pathToExport = provisionerFactory.getProvisioner(description.getServiceType()).getWorkdirToSave();
        return pathToExport.isPresent() && ServiceStatus.toBeCheckedForExport()
                .contains(getServiceStatus(description.getId()));
    }

    private ServiceStatus getServiceStatus(long serviceDescriptionId) {
        return descriptionRepository.getById(serviceDescriptionId).getStatus();
    }

    public ScheduleRequestEntity fillVmResourceId(long scheduleRequestId, long vmResourceId) {
        var scheduleRequestEntity = requestRepository.getById(scheduleRequestId);
        scheduleRequestEntity.setVmResourceId(vmResourceId);
        requestRepository.save(scheduleRequestEntity);
        return scheduleRequestEntity;
    }

    public void stopTracking(long scheduleRequestId) {
        requestRepository.findById(scheduleRequestId).ifPresent(requestEntity -> {
            var serviceDescriptions = requestEntity.getServiceDescriptions();
            serviceDescriptions.forEach(description -> description.setStatus(ServiceStatus.CANCELLED));
            descriptionRepository.saveAll(serviceDescriptions);
        });
    }
}
