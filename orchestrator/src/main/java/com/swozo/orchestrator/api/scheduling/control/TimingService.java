package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class TimingService {
    public static final int MANUAL_CLEANUP_SECONDS = 2 * 60 * 60;
    private final ScheduleRequestTracker requestTracker;

    public long getSchedulingOffset(ScheduleRequestEntity request, int schedulingSeconds) {
        return offsetTime(request.getStartTime()) - schedulingSeconds;
    }

    public long getExportOffset(ScheduleRequestEntity request) {
        return offsetTime(request.getEndTime());
    }

    public long getDeletionOffset(ScheduleRequestEntity request, int cleanupSeconds) {
        if (requestTracker.canBeImmediatelyDeleted(request.getId())) {
            return 0;
        } else {
            return offsetTime(request.getEndTime()) + cleanupSeconds;
        }
    }

    private long offsetTime(LocalDateTime targetTime) {
        return nonNegative(targetTime.toEpochSecond(ZoneOffset.UTC)
                - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
    }

    private long nonNegative(long number) {
        return Math.max(0, number);
    }
}
