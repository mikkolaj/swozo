package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.orchestrator.api.links.persistence.mapper.ActivityLinkInfoMapper;
import com.swozo.orchestrator.api.links.persistence.repository.ActivityLinkInfoRepository;
import com.swozo.orchestrator.api.scheduling.persistence.entity.RequestStatus;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ScheduleRequestMapper;
import com.swozo.orchestrator.api.scheduling.persistence.repository.ScheduleRequestRepository;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisionerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleRequestTracker {
    private final ScheduleRequestRepository requestRepository;
    private final ActivityLinkInfoRepository linkRepository;
    private final ScheduleRequestMapper requestMapper;
    private final ActivityLinkInfoMapper linkMapper;
    private final TimedSoftwareProvisionerFactory provisionerFactory;

    public ScheduleRequestEntity startTracking(ScheduleRequest scheduleRequest) {
        var entity = requestRepository.save(requestMapper.toPersistence(scheduleRequest));
        Hibernate.initialize(entity.getDynamicProperties());
        return entity;
    }

    public List<ScheduleRequestEntity> getSchedulesToDelete() {
        var timeThreshold = LocalDateTime.now().minusSeconds(TimedSoftwareProvisioner.MAX_PROVISIONING_SECONDS);
        return requestRepository.findByEndTimeLessThanAndStatusNot(timeThreshold, RequestStatus.DELETED)
                .stream()
                .filter(this::endsBeforeAvailability)
                .toList();
    }

    public List<ScheduleRequestEntity> getValidSchedulesToRestartFromBeginning() {
        var timeThreshold = getFurthestProvisioningThreshold();
        var submittedSchedules = getValidSchedulesWithStatus(timeThreshold, RequestStatus.SUBMITTED);
        var schedulesFailedDuringVmCreation = getValidSchedulesWithStatus(timeThreshold, RequestStatus.VM_CREATING);
        var schedulesFailedOnVmCreation = getValidSchedulesWithStatus(timeThreshold, RequestStatus.VM_CREATION_FAILED);

        return combineLists(submittedSchedules, schedulesFailedDuringVmCreation, schedulesFailedOnVmCreation);
    }

    public List<ScheduleRequestEntity> getValidSchedulesToReprovision() {
        var timeThreshold = getFurthestProvisioningThreshold();
        var submittedSchedules = getValidSchedulesWithStatus(timeThreshold, RequestStatus.PROVISIONING);
        var schedulesFailedOnVmCreation = getValidSchedulesWithStatus(timeThreshold, RequestStatus.PROVISIONING_FAILED);

        return combineLists(submittedSchedules, schedulesFailedOnVmCreation);
    }

    public List<ScheduleRequestEntity> getValidReadySchedules() {
        var timeThreshold = getFurthestProvisioningThreshold();
        return getValidSchedulesWithStatus(timeThreshold, RequestStatus.READY);
    }

    private LocalDateTime getFurthestProvisioningThreshold() {
        return LocalDateTime.now().minusSeconds(TimedSoftwareProvisioner.MAX_PROVISIONING_SECONDS);
    }

    private List<ScheduleRequestEntity> getValidSchedulesWithStatus(LocalDateTime timeThreshold, RequestStatus status) {
        return requestRepository.findByEndTimeGreaterThanAndStatusEquals(timeThreshold, status)
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
        var dto = requestMapper.toDto(requestEntity);
        var provisioningSeconds = provisionerFactory.getProvisioner(dto.scheduleType()).getProvisioningSeconds();
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
        return linkRepository.findAllByScheduleRequestId(scheduleRequestId).stream().map(linkMapper::toDto).toList();
    }

    public void saveLinks(Long scheduleRequestId, List<ActivityLinkInfo> links) {
        var requestEntity = requestRepository.getById(scheduleRequestId);
        var linkEntities = links.stream().map(link -> linkMapper.toPersistence(link, requestEntity)).toList();
        linkRepository.saveAll(linkEntities);
    }
}
