package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ScheduleRequest;
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
    private ScheduleRequestRepository requestRepository;
    private ActivityLinkInfoRepository linkRepository;
    private ScheduleRequestMapper requestMapper;
    private ActivityLinkInfoMapper linkMapper;

    public long persist(ScheduleRequest scheduleRequest) {
        var result = requestRepository.save(requestMapper.toPersistence(scheduleRequest));
        return result.getId();
    }

    public void unpersist(Long scheduleRequestId) {
        requestRepository.deleteById(scheduleRequestId);
    }

    public List<ActivityLinkInfo> getLinks(Long scheduleRequestId) {
        return linkRepository.findAllByScheduleRequestId(scheduleRequestId).stream().map(linkMapper::toDto).toList();
    }

    public void saveLinks(List<ActivityLinkInfo> links, Long scheduleRequestId) {
        var linkEntities = links.stream().map(link -> linkMapper.toPersistence(link, scheduleRequestId)).toList();
        linkRepository.saveAll(linkEntities);
    }

    private record RequestWithLinks(ScheduleRequest scheduleRequest, List<ActivityLinkInfo> links) {
    }
}
