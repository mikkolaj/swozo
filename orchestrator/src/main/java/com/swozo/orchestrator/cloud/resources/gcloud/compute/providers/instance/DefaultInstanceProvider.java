package com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.instance;

import com.google.cloud.compute.v1.AttachedDisk;
import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.NetworkInterface;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMAddress;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultInstanceProvider implements InstanceProvider {
    private final MachineTypeProvider machineTypeProvider;

    public Instance createInstance(VMAddress vmAddress, VMSpecs vmSpecs, AttachedDisk disk,
            NetworkInterface networkInterface) {
        var machineType = machineTypeProvider.constructMachineType(vmAddress.zone(), vmSpecs.machineType());

        return Instance.newBuilder()
                .setName(vmAddress.vmName())
                .setMachineType(machineType)
                .setSourceMachineImage("https://www.googleapis.com/compute/v1/projects/swozo-362919/global/machineImages/e2-medium-docker")
                .addDisks(disk)
                .addNetworkInterfaces(networkInterface)
                .build();
    }
}
