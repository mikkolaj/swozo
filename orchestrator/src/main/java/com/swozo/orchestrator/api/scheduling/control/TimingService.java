package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TimingService {
    public static final int MANUAL_CLEANUP_SECONDS = 2 * 60 * 60;

    public long getSchedulingOffset(ScheduleRequestEntity request, int schedulingSeconds) {
        return offsetTime(request.getStartTime()) - schedulingSeconds;
    }

    public long getExportOffset(ScheduleRequestEntity request) {
        return offsetTime(request.getEndTime());
    }

    public long getDeletionOffset(ScheduleRequestEntity request, int exportSeconds) {
        return offsetTime(request.getEndTime()) + exportSeconds;
    }

    public long getExtendedDeletionOffset(ScheduleRequestEntity request) {
        return offsetTime(request.getEndTime()) + 2 * 60 * 60;
    }

    private long offsetTime(LocalDateTime targetTime) {
        return nonNegative(targetTime.toEpochSecond(ZoneOffset.UTC)
                - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
    }

    private long nonNegative(long number) {
        return Math.max(0, number);
    }
}
