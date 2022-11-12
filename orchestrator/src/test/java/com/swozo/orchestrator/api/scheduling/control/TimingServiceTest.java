package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TimingServiceTest {

    @Test
    void getDeletionOffset_EndTimeInThePast_ReturnsNonNegativeOffsetShiftedByCleanupSeconds() {
        var timingService = new TimingService();
        var scheduleRequest = Mockito.mock(ScheduleRequestEntity.class);
        var cleanupSeconds = 10;
        when(scheduleRequest.getEndTime()).thenReturn(LocalDateTime.now().minusDays(365));

        assertEquals(cleanupSeconds, timingService.getDeletionOffset(scheduleRequest, cleanupSeconds));
    }
}