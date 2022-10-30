package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.properties.ScheduleType;
import com.swozo.orchestrator.api.scheduling.persistence.entity.RequestStatus;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ScheduleRequestMapper;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ScheduleTypeMapper;
import com.swozo.orchestrator.api.scheduling.persistence.repository.ScheduleRequestRepository;
import com.swozo.orchestrator.cloud.resources.vm.TimedVMProvider;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisionerFactory;
import com.swozo.utils.CheckedExceptionConverter;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleRequestTracker {
    private final ScheduleRequestRepository requestRepository;
    private final ScheduleRequestMapper requestMapper;
    private final ScheduleTypeMapper scheduleTypeMapper;
    private final TimedSoftwareProvisionerFactory provisionerFactory;
    private final TimedVMProvider vmProvider;

    public ScheduleRequestEntity startTracking(ScheduleRequest scheduleRequest) {
        var entity = requestRepository.save(requestMapper.toPersistence(scheduleRequest));
        Hibernate.initialize(entity.getDynamicProperties());
        return entity;
    }

    public List<ScheduleRequestEntity> getSchedulesToDelete() {
        return requestRepository.findByEndTimeLessThanAndStatusNot(getFurthestProvisioningThreshold(), RequestStatus.DELETED)
                .stream()
                .filter(this::endsBeforeAvailability)
                .toList();
    }

    public List<ScheduleRequestEntity> getValidSchedulesToRestartFromBeginning() {
        var submittedSchedules = getValidSchedulesWithStatus(RequestStatus.SUBMITTED);
        var schedulesFailedDuringVmCreation = getValidSchedulesWithStatus(RequestStatus.VM_CREATING);
        var schedulesFailedOnVmCreation = getValidSchedulesWithStatus(RequestStatus.VM_CREATION_FAILED);

        return combineLists(submittedSchedules, schedulesFailedDuringVmCreation, schedulesFailedOnVmCreation);
    }

    public List<ScheduleRequestEntity> getValidSchedulesToReprovision() {
        var submittedSchedules = getValidSchedulesWithStatus(RequestStatus.PROVISIONING);
        var schedulesFailedOnVmCreation = getValidSchedulesWithStatus(RequestStatus.PROVISIONING_FAILED);

        return combineLists(submittedSchedules, schedulesFailedOnVmCreation);
    }

    public List<ScheduleRequestEntity> getValidReadySchedules() {
        return getValidSchedulesWithStatus(RequestStatus.READY);
    }

    private LocalDateTime getFurthestProvisioningThreshold() {
        return LocalDateTime.now().plusSeconds(TimedSoftwareProvisioner.MAX_PROVISIONING_SECONDS);
    }

    private List<ScheduleRequestEntity> getValidSchedulesWithStatus(RequestStatus status) {
        return requestRepository.findByEndTimeGreaterThanAndStatusEquals(LocalDateTime.now(), status)
                .stream()
                .filter(this::endsAfterAvailability)
                .toList();
    }

    @SafeVarargs
    private <T> List<T> combineLists(List<T>... lists) {
        var combined = new ArrayList<T>();
        for (var list : lists) {
            combined.addAll(list);
        }
        return combined;
    }

    private boolean endsBeforeAvailability(ScheduleRequestEntity requestEntity) {
        return requestEntity.getEndTime()
                .isAfter(getTargetAvailability(requestEntity));
    }

    private boolean endsAfterAvailability(ScheduleRequestEntity scheduleRequestEntity) {
        return scheduleRequestEntity.getEndTime()
                .isBefore(getTargetAvailability(scheduleRequestEntity));
    }


    private LocalDateTime getTargetAvailability(ScheduleRequestEntity requestEntity) {
        var scheduleType = scheduleTypeMapper.toDto(requestEntity.getScheduleType());
        var provisioningSeconds = provisionerFactory.getProvisioner(scheduleType).getProvisioningSeconds();
        return LocalDateTime.now().plusSeconds(provisioningSeconds);
    }

    public void updateStatus(long scheduleRequestId, RequestStatus status) {
        var scheduleRequestEntity = requestRepository.getById(scheduleRequestId);
        scheduleRequestEntity.setStatus(status);
        requestRepository.save(scheduleRequestEntity);
    }

    public void markAsFailure(long scheduleRequestId) {
        var scheduleRequestEntity = requestRepository.findById(scheduleRequestId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No request with id: [%s] in DB.", scheduleRequestId)));
        scheduleRequestEntity.setStatus(scheduleRequestEntity.getStatus().getNextErrorStatus());
        requestRepository.save(scheduleRequestEntity);
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
                .flatMap(this::toRequestTypeWithVmResourceId)
                .flatMap(this::fetchLinks)
                .orElse(Collections.emptyList());
    }

    private Optional<List<ActivityLinkInfo>> fetchLinks(RequestTypeWithVmResourceId request) {
        return CheckedExceptionConverter.from(() -> vmProvider.getVMResourceDetails(request.vmResourceId).get())
                .get()
                .map(provisionerFactory.getProvisioner(request.scheduleType)::createLinks);
    }

    private Optional<RequestTypeWithVmResourceId> toRequestTypeWithVmResourceId(ScheduleRequestEntity requestEntity) {
        return requestEntity.getVmResourceId().map(id -> new RequestTypeWithVmResourceId(
                scheduleTypeMapper.toDto(requestEntity.getScheduleType()),
                id
        ));
    }

    private record RequestTypeWithVmResourceId(ScheduleType scheduleType, long vmResourceId) {
    }
}
