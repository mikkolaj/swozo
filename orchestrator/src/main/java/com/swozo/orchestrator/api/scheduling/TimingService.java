package com.swozo.orchestrator.api.scheduling;

import com.swozo.model.scheduling.ScheduleRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TimingService {
    public long getScheludingOffset(ScheduleRequest request, int schedulingSeconds) {
        return offsetTime(request.getServiceLifespan().startTime()) - schedulingSeconds;
    }

    public long getDeletionOffset(ScheduleRequest request, int cleanupSeconds) {
        return offsetTime(request.getServiceLifespan().endTime()) + cleanupSeconds;
    }

    private long offsetTime(LocalDateTime targetTime) {
        return targetTime.toEpochSecond(ZoneOffset.UTC)
                - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
}
