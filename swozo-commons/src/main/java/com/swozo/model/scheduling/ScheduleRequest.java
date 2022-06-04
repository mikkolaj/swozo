package com.swozo.model.scheduling;

import com.swozo.model.Psm;
import com.swozo.model.ServiceLifespan;
import lombok.*;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class ScheduleRequest {
    private final ServiceLifespan serviceLifespan;
    private final Psm psm;
}
