package com.swozo.orchestrator.cloud.software;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceTypeEntity;
import com.swozo.orchestrator.cloud.resources.vm.VmResourceDetails;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TimedSoftwareProvisioner {
    int MAX_PROVISIONING_SECONDS = 60 * 60 * 3;

    CompletableFuture<List<ActivityLinkInfo>> provision(VmResourceDetails resourceDetails, Map<String, String> dynamicParameters);

    CompletableFuture<List<ActivityLinkInfo>> createLinks(VmResourceDetails resourceDetails);

    void validateParameters(Map<String, String> dynamicParameters) throws InvalidParametersException;

    ServiceTypeEntity getScheduleType();

    ServiceConfig getServiceConfig();

    int getProvisioningSeconds();

    Optional<String> getWorkdirToSave();

}
