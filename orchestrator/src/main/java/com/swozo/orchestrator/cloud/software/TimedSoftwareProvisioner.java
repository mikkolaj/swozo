package com.swozo.orchestrator.cloud.software;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceTypeEntity;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TimedSoftwareProvisioner {
    int MAX_PROVISIONING_SECONDS = 60 * 60 * 3;

    List<ActivityLinkInfo> provision(VMResourceDetails resourceDetails, Map<String, String> dynamicParameters) throws InterruptedException, ProvisioningFailed;

    List<ActivityLinkInfo> createLinks(VMResourceDetails resourceDetails);

    void validateParameters(Map<String, String> dynamicParameters) throws InvalidParametersException;

    ServiceTypeEntity getScheduleType();

    ServiceConfig getServiceConfig();

    int getProvisioningSeconds();
}
