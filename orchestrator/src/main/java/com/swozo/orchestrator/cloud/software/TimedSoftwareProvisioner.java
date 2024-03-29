package com.swozo.orchestrator.cloud.software;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceDescriptionEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceTypeEntity;
import com.swozo.orchestrator.cloud.resources.vm.VmResourceDetails;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TimedSoftwareProvisioner {
    CompletableFuture<List<ActivityLinkInfo>> provision(
            ScheduleRequestEntity requestEntity,
            ServiceDescriptionEntity description,
            VmResourceDetails resourceDetails
    );

    void validateParameters(Map<String, String> dynamicParameters) throws InvalidParametersException;

    ServiceTypeEntity getServiceType();

    ServiceConfig getServiceConfig();

    int getProvisioningSeconds(Map<String, String> dynamicParameters);

    Optional<String> getWorkdirToSave(Map<String, String> dynamicParameters);

}
