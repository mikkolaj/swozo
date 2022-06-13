package com.swozo.orchestrator.configuration.conditions;

import com.swozo.orchestrator.configuration.CloudProvider;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Optional;

public class GCloudCondition implements Condition {
    private static final String CLOUD_PROVIDER_PROPERTY = "cloud-provider";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        var possibleProperty = context.getEnvironment().getProperty(CLOUD_PROVIDER_PROPERTY);
        return Optional.ofNullable(possibleProperty)
                .map(property -> property.equals(CloudProvider.GCLOUD.toString()))
                .orElse(false);
    }
}
