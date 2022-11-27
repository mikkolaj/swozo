package com.swozo.persistence.mda.policies;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PolicyType {
    MAX_VCPU(4, true),
    MAX_RAM_GB(4, true),
    MAX_DISK_GB(32, true),
    MAX_BANDWIDTH_MBPS(8192, true),
    MAX_STUDENTS(50, false),
    MAX_ACTIVITY_DURATION_MINUTES(180, false),
    MAX_PARALLEL_SANDBOXES(1, false),
    ;

    private final int defaultValue;
    private final boolean mdaDependant;

    public boolean isMdaDependant() {
        return mdaDependant;
    }
}
