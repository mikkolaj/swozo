package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ActivityLinkInfoMapper;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ScheduleRequestMapper;
import com.swozo.orchestrator.api.scheduling.persistence.repository.ActivityLinkInfoRepository;
import com.swozo.orchestrator.api.scheduling.persistence.repository.ScheduleRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleRequestTracker {
    private final ScheduleRequestRepository requestRepository;
    private final ActivityLinkInfoRepository linkRepository;
    private final ScheduleRequestMapper requestMapper;
    private final ActivityLinkInfoMapper linkMapper;

    public ScheduleRequestEntity persist(ScheduleRequest scheduleRequest) {
        return requestRepository.save(requestMapper.toPersistence(scheduleRequest));
    }

    public void unpersist(Long scheduleRequestId) {
        requestRepository.deleteById(scheduleRequestId);
    }

    public List<ActivityLinkInfo> getLinks(Long scheduleRequestId) {
        return linkRepository.findAllByScheduleRequestId(scheduleRequestId).stream().map(linkMapper::toDto).toList();
    }

    public void saveLinks(List<ActivityLinkInfo> links, Long scheduleRequestId) {
        var requestEntity = requestRepository.getById(scheduleRequestId);
        var linkEntities = links.stream().map(link -> linkMapper.toPersistence(link, requestEntity)).toList();
        linkRepository.saveAll(linkEntities);
    }
}