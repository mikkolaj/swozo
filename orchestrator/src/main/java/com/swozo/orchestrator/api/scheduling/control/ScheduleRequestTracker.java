package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceDescriptionEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ScheduleRequestMapper;
import com.swozo.orchestrator.api.scheduling.persistence.repository.ScheduleRequestRepository;
import com.swozo.orchestrator.api.scheduling.persistence.repository.ServiceDescriptionRepository;
import com.swozo.orchestrator.cloud.resources.vm.TimedVmProvider;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisionerFactory;
import com.swozo.utils.CheckedExceptionConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

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

    public boolean canBeProvisioned(long serviceDescriptionId) {
        return ServiceStatus.provisioning().contains(getServiceStatus(serviceDescriptionId));
    }

    public boolean wasNotExported(ServiceDescriptionEntity description) {
        return ServiceStatus.withVmBeforeExport().contains(description.getStatus());
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

    public List<ActivityLinkInfo> getLinks(long scheduleRequestId) {
        return requestRepository
                .findById(scheduleRequestId)
                .stream()
                .flatMap(this::toFullServiceInfo)
                .flatMap(this::fetchLinks)
                .toList();
    }

    private Stream<ActivityLinkInfo> fetchLinks(FullServiceInfo request) {
        return CheckedExceptionConverter.from(() ->
                vmProvider.getVMResourceDetails(request.vmResourceId)
                        .thenCompose(details -> provisionerFactory.getProvisioner(request.description.getServiceType())
                                .createLinks(request.requestEntity, request.description, details)
                        ).exceptionally(ex -> Collections.emptyList())
                        .get()
        ).get().stream();
    }

    private Stream<FullServiceInfo> toFullServiceInfo(ScheduleRequestEntity requestEntity) {
        return requestEntity.getVmResourceId().stream().flatMap(id ->
                requestEntity.getServiceDescriptions().stream().map(description ->
                        new FullServiceInfo(requestEntity, description, id)
                )
        );
    }

    private record FullServiceInfo(
            ScheduleRequestEntity requestEntity,
            ServiceDescriptionEntity description,
            long vmResourceId
    ) {
    }

}
