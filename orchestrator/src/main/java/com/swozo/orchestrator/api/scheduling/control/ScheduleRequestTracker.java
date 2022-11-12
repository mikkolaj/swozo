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
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleRequestTracker {
    private final ScheduleRequestRepository requestRepository;
    private final ServiceDescriptionRepository descriptionRepository;
    private final ScheduleRequestMapper requestMapper;
    private final TimedSoftwareProvisionerFactory provisionerFactory;
    private final TimedVmProvider vmProvider;

    public ScheduleRequestEntity startTracking(ScheduleRequest scheduleRequest) {
        var entity = requestRepository.save(requestMapper.toPersistence(scheduleRequest));
        initializeParameters(List.of(entity));
        return entity;
    }

    public List<ScheduleRequestEntity> getSchedulesToDelete() {
        var provisioned = requestRepository
                .findScheduleRequestsWithAllServiceDescriptionsInStatus(
                        ServiceStatus.asStrings(ServiceStatus.readyToBeDeleted())
                );

        return initializeParameters(provisioned);
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

        return initializeParameters(schedulesToRestart);
    }

    public List<ScheduleRequestEntity> getSchedulesWithVmBeforeExport() {
        var provisioned = requestRepository
                .findByServiceDescriptions_StatusIn(ServiceStatus.withVmBeforeExport());

        return initializeParameters(provisioned);
    }

    private List<ScheduleRequestEntity> getValidSchedulesWithStatusIn(Set<ServiceStatus> statuses) {
        return requestRepository.findByEndTimeGreaterThanAndServiceDescriptions_StatusIn(LocalDateTime.now(), statuses)
                .stream()
                .toList();
    }

    private List<ScheduleRequestEntity> initializeParameters(List<ScheduleRequestEntity> entities) {
        var serviceDescriptions =
                entities.stream().map(ScheduleRequestEntity::getServiceDescriptions).flatMap(Collection::stream);
        serviceDescriptions.forEach(description -> {
            Hibernate.initialize(description);
            Hibernate.initialize(description.getDynamicProperties());
        });
        return entities;
    }

    public void updateStatus(ScheduleRequestEntity scheduleRequestEntity, ServiceStatus status) {
        var descriptions = scheduleRequestEntity.getServiceDescriptions().stream().map(description -> {
            description.setStatus(status);
            return description;
        }).toList();
        descriptionRepository.saveAll(descriptions);
    }

    public void updateStatus(ServiceDescriptionEntity serviceDescriptionEntity, ServiceStatus status) {
        serviceDescriptionEntity.setStatus(status);
        descriptionRepository.save(serviceDescriptionEntity);
    }

    public void markAsFailure(ServiceDescriptionEntity serviceDescription) {
        serviceDescription.setStatus(serviceDescription.getStatus().getNextErrorStatus());
        descriptionRepository.save(serviceDescription);
    }

    public void fillVmResourceId(long scheduleRequestId, long vmResourceId) {
        var scheduleRequestEntity = requestRepository.getById(scheduleRequestId);
        scheduleRequestEntity.setVmResourceId(vmResourceId);
        requestRepository.save(scheduleRequestEntity);
    }

    // TODO: use this sometime, maybe after a while it'd be nice to clean the db from old requests
    public void stopTracking(Long scheduleRequestId) {
        requestRepository.deleteById(scheduleRequestId);
    }

    public List<ActivityLinkInfo> getLinks(Long scheduleRequestId) {
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
