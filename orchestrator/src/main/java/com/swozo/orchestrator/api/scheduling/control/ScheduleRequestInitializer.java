package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceDescriptionEntity;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ScheduleRequestInitializer {

    public List<ScheduleRequestEntity> initializeParameters(List<ScheduleRequestEntity> entities) {
        var serviceDescriptions =
                entities.stream().map(ScheduleRequestEntity::getServiceDescriptions).flatMap(Collection::stream);
        initializeDescriptions(serviceDescriptions);
        return entities;
    }

    public ScheduleRequestEntity initializeParameters(ScheduleRequestEntity entity) {
        var serviceDescriptions = entity.getServiceDescriptions().stream();
        initializeDescriptions(serviceDescriptions);
        return entity;
    }

    public ServiceDescriptionEntity initializeServiceDescription(ServiceDescriptionEntity serviceDescription) {
        initializeDescriptions(Stream.of(serviceDescription));
        return serviceDescription;
    }

    private void initializeDescriptions(Stream<ServiceDescriptionEntity> serviceDescriptions) {
        serviceDescriptions.forEach(description -> {
            Hibernate.initialize(description);
            Hibernate.initialize(description.getDynamicProperties());
        });
    }

}
