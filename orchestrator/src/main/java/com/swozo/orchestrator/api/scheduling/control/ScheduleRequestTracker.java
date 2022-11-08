package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceDescriptionEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceTypeEntity;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ScheduleRequestMapper;
import com.swozo.orchestrator.api.scheduling.persistence.repository.ScheduleRequestRepository;
import com.swozo.orchestrator.api.scheduling.persistence.repository.ServiceDescriptionRepository;
import com.swozo.orchestrator.cloud.resources.vm.TimedVMProvider;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleRequestTracker {
    private final ScheduleRequestRepository requestRepository;
    private final ServiceDescriptionRepository descriptionRepository;
    private final ScheduleRequestMapper requestMapper;
    private final TimedSoftwareProvisionerFactory provisionerFactory;
    private final TimedVMProvider vmProvider;

    public ScheduleRequestEntity startTracking(ScheduleRequest scheduleRequest) {
        var entity = requestRepository.save(requestMapper.toPersistence(scheduleRequest));
        initializeParameters(List.of(entity));
        return entity;
    }

    public List<ScheduleRequestEntity> getUnfulfilledSchedulesToDelete() {
        // At least one SD wasn't provisioned
        return requestRepository.findByEndTimeLessThanAndServiceDescriptions_StatusIn(getFurthestProvisioningThreshold(), ServiceStatus.notYetReady())
                .stream()
                .flatMap(this::toScheduleRequestsWithServiceTypes)
                .filter(this::endsBeforeAvailability)
                .map(ScheduleRequestWithServiceType::request)
                .toList();
    }

    public List<ScheduleRequestEntity> getValidSchedulesToRestartFromBeginning() {
        var schedulesToRestart = getValidSchedulesWithStatusIn(ServiceStatus.withoutVm());

        return initializeParameters(schedulesToRestart);
    }

    public List<ScheduleRequestEntity> getValidSchedulesToReprovision() {
        var submittedSchedules = getValidSchedulesWithStatusIn(ServiceStatus.provisioning());

        return initializeParameters(submittedSchedules);
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

    public List<ScheduleRequestEntity> getSchedulesToCleanAndDelete() {
        var schedules = requestRepository
                .findByServiceDescriptions_AllStatusIn(ServiceStatus.toBeCleanedAndTerminated())
                .stream()
                .toList();

        return initializeParameters(schedules);
    }

    public List<ScheduleRequestEntity> getSchedulesToDelete() {
        var schedules = requestRepository
                .findByServiceDescriptions_StatusEquals(ServiceStatus.toBeDeleted())
                .stream()
                .toList();

        return initializeParameters(schedules);
    }

    private LocalDateTime getFurthestProvisioningThreshold() {
        return LocalDateTime.now().plusSeconds(TimedSoftwareProvisioner.MAX_PROVISIONING_SECONDS);
    }

    private List<ScheduleRequestEntity> getValidSchedulesWithStatusIn(Set<ServiceStatus> statuses) {
        return requestRepository.findByEndTimeGreaterThanAndServiceDescriptions_StatusIn(LocalDateTime.now(), statuses)
                .stream()
                .flatMap(this::toScheduleRequestsWithServiceTypes)
                .filter(this::endsAfterAvailability)
                .map(ScheduleRequestWithServiceType::request)
                .distinct()
                .toList();
    }

    private boolean endsBeforeAvailability(ScheduleRequestWithServiceType scheduleData) {
        return scheduleData.request.getEndTime()
                .isBefore(getTargetAvailability(scheduleData.serviceType));
    }

    private boolean endsAfterAvailability(ScheduleRequestWithServiceType scheduleData) {
        return scheduleData.request.getEndTime()
                .isAfter(getTargetAvailability(scheduleData.serviceType));
    }


    private LocalDateTime getTargetAvailability(ServiceTypeEntity serviceType) {
        var provisioningSeconds = provisionerFactory.getProvisioner(serviceType).getProvisioningSeconds();
        return LocalDateTime.now().plusSeconds(provisioningSeconds);
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
                .flatMap(this::toServiceTypesWithVmResourceIds)
                .flatMap(this::fetchLinks)
                .toList();
    }

    private Stream<ActivityLinkInfo> fetchLinks(RequestTypeWithVmResourceId request) {
        return CheckedExceptionConverter.from(() ->
                vmProvider.getVMResourceDetails(request.vmResourceId)
                        .thenCompose(possibleDetails ->
                                possibleDetails.map(details ->
                                        provisionerFactory.getProvisioner(request.serviceType)
                                                .createLinks(details)
                                ).orElse(CompletableFuture.completedFuture(Collections.emptyList()))
                        ).get()
        ).get().stream();
    }

    private Stream<RequestTypeWithVmResourceId> toServiceTypesWithVmResourceIds(ScheduleRequestEntity requestEntity) {
        return requestEntity.getVmResourceId().stream().flatMap(id ->
                requestEntity.getServiceDescriptions().stream().map(description ->
                        new RequestTypeWithVmResourceId(description.getServiceType(), id)
                )
        );
    }

    private Stream<ScheduleRequestWithServiceType> toScheduleRequestsWithServiceTypes(ScheduleRequestEntity requestEntity) {
        return requestEntity.getServiceDescriptions().stream().map(description ->
                new ScheduleRequestWithServiceType(requestEntity, description.getServiceType())
        );
    }

    private record RequestTypeWithVmResourceId(ServiceTypeEntity serviceType, long vmResourceId) {
    }

    private record ScheduleRequestWithServiceType(ScheduleRequestEntity request, ServiceTypeEntity serviceType) {
    }
}
