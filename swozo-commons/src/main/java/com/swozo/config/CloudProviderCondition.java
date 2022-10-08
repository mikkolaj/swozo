package com.swozo.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Optional;

public abstract class CloudProviderCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        var possibleProperty = context.getEnvironment().getProperty(getProviderProperty());
        return Optional.ofNullable(possibleProperty)
                .map(property -> property.equals(getCloudProvider().toString()))
                .orElse(false);
    }

    public abstract String getProviderProperty();

    public abstract CloudProvider getCloudProvider();
}
