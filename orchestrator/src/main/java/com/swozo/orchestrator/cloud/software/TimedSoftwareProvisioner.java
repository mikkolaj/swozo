package com.swozo.orchestrator.cloud.software;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.model.scheduling.properties.ScheduleType;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;

import java.util.List;
import java.util.Map;

public interface TimedSoftwareProvisioner {
    List<ActivityLinkInfo> provision(
            VMResourceDetails resourceDetails,
            Map<String, String> dynamicParameters,
            String provisionerVersion
    ) throws InterruptedException, ProvisioningFailed;

    void validateParameters(Map<String, String> dynamicParameters, String provisionerVersion) throws InvalidParametersException;

    ScheduleType getScheduleType();

    ServiceConfig getServiceConfig();

    int getProvisioningSeconds();
}
