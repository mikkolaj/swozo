package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TimingServiceTest {

    @Test
    void getDeletionOffset_EndTimeInThePastAndRequestNotCancelled_ReturnsNonNegativeOffsetShiftedByCleanupSeconds() {
        var mockRequestTracker = Mockito.mock(ScheduleRequestTracker.class);
        var scheduleRequest = Mockito.mock(ScheduleRequestEntity.class);
        var cleanupSeconds = 10;
        var requestId = 1L;
        when(scheduleRequest.getId()).thenReturn(requestId);
        when(scheduleRequest.getEndTime()).thenReturn(LocalDateTime.now().minusDays(365));
        when(mockRequestTracker.canBeImmediatelyDeleted(requestId)).thenReturn(false);
        var timingService = new TimingService(mockRequestTracker);

        assertEquals(cleanupSeconds, timingService.getDeletionOffset(scheduleRequest, cleanupSeconds));
    }

    @Test
    void getDeletionOffset_ScheduleIsCancelled_ReturnZeroSeconds() {
        var scheduleRequest = Mockito.mock(ScheduleRequestEntity.class);
        var mockRequestTracker = Mockito.mock(ScheduleRequestTracker.class);
        var cleanupSeconds = 10;
        var requestId = 1L;
        when(scheduleRequest.getEndTime()).thenReturn(LocalDateTime.now().minusDays(365));
        when(scheduleRequest.getId()).thenReturn(requestId);
        when(mockRequestTracker.canBeImmediatelyDeleted(requestId)).thenReturn(true);
        var timingService = new TimingService(mockRequestTracker);

        assertEquals(0, timingService.getDeletionOffset(scheduleRequest, cleanupSeconds));
    }
}