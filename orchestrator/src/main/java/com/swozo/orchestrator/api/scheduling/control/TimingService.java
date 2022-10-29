package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TimingService {
    public long getSchedulingOffset(ScheduleRequestEntity request, int schedulingSeconds) {
        return offsetTime(request.getEndTime()) - schedulingSeconds;
    }

    public long getDeletionOffset(ScheduleRequestEntity request, int cleanupSeconds) {
        return offsetTime(request.getEndTime()) + cleanupSeconds;
    }

    private long offsetTime(LocalDateTime targetTime) {
        return targetTime.toEpochSecond(ZoneOffset.UTC)
                - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
}
