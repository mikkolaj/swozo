package com.swozo.orchestrator.api.scheduling.control.recovery;

import com.swozo.orchestrator.api.scheduling.control.ScheduleRequestInitializer;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceDescriptionEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus;
import com.swozo.orchestrator.api.scheduling.persistence.repository.ScheduleRequestRepository;
import com.swozo.orchestrator.api.scheduling.persistence.repository.ServiceDescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FailedSchedulesFinder {
    private final ScheduleRequestRepository requestRepository;
    private final ServiceDescriptionRepository serviceRepository;
    private final ScheduleRequestInitializer initializer;

    public List<ScheduleRequestEntity> getFailedDuringVmCreation() {
        return requestRepository
                .findScheduleRequestsWithAllServiceDescriptionsInStatus(ServiceStatus.PROVISIONING_FAILED.asString())
                .stream()
                .map(initializer::initializeParameters)
                .toList();
    }

    public Map<ScheduleRequestEntity, ServicesToRetry> getFailedSchedulesWithVm() {
        return serviceRepository.findAllByStatusIn(Set.of(ServiceStatus.PROVISIONING_FAILED, ServiceStatus.EXPORT_FAILED))
                .stream()
                .collect(Collectors.groupingBy(ServiceDescriptionEntity::getScheduleRequest))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        this::splitServicesBySchedulingStage
                ));
    }

    private ServicesToRetry splitServicesBySchedulingStage(Map.Entry<ScheduleRequestEntity, List<ServiceDescriptionEntity>> entry) {
        var toReprovision = getServicesInStatusAndInitialize(entry, ServiceStatus.PROVISIONING_FAILED);
        var toExport = getServicesInStatusAndInitialize(entry, ServiceStatus.EXPORT_FAILED);

        return new ServicesToRetry(toReprovision, toExport);
    }

    private List<ServiceDescriptionEntity> getServicesInStatusAndInitialize(Map.Entry<ScheduleRequestEntity, List<ServiceDescriptionEntity>> entry, ServiceStatus provisioningFailed) {
        return entry.getValue()
                .stream()
                .filter(service -> service.getStatus() == provisioningFailed)
                .map(initializer::initializeServiceDescription)
                .toList();
    }

    public record ServicesToRetry(List<ServiceDescriptionEntity> toReprovision,
                                  List<ServiceDescriptionEntity> toExport) {
    }
}
