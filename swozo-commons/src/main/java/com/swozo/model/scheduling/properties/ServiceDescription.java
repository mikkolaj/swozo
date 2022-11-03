package com.swozo.model.scheduling.properties;

import java.util.Map;

public record ServiceDescription(ServiceType serviceType, Map<String, String> dynamicProperties) {
}
