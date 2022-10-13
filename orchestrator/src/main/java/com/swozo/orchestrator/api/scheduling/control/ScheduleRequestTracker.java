package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.orchestrator.api.links.persistence.mapper.ActivityLinkInfoMapper;
import com.swozo.orchestrator.api.links.persistence.repository.ActivityLinkInfoRepository;
import com.swozo.orchestrator.api.scheduling.persistence.entity.RequestStatus;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ScheduleRequestMapper;
import com.swozo.orchestrator.api.scheduling.persistence.repository.ScheduleRequestRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleRequestTracker {
    private final ScheduleRequestRepository requestRepository;
    private final ActivityLinkInfoRepository linkRepository;
    private final ScheduleRequestMapper requestMapper;
    private final ActivityLinkInfoMapper linkMapper;

    public ScheduleRequestEntity startTracking(ScheduleRequest scheduleRequest) {
        var entity = requestRepository.save(requestMapper.toPersistence(scheduleRequest));
        Hibernate.initialize(entity.getDynamicProperties());
        return entity;
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
