package com.swozo.model.scheduling.properties;

import java.util.Map;

public record ServiceDescription(
        Long serviceModuleId,
        ServiceType serviceType,
        Map<String, String> dynamicProperties
) {
}
