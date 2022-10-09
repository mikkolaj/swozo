package com.swozo.orchestrator.cloud.software;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;

import java.util.List;
import java.util.Map;

public interface TimedSoftwareProvisioner {
    List<ActivityLinkInfo> provision(VMResourceDetails resourceDetails, Map<String, String> dynamicParameters) throws InterruptedException, ProvisioningFailed;
    void validateParameters(Map<String, String> dynamicParameters) throws InvalidParametersException;
    List<ParameterDescription> getParameterDescriptions();

    int getProvisioningSeconds();
}
