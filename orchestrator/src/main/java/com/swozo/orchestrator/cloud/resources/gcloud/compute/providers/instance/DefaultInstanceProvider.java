package com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.instance;

import com.google.cloud.compute.v1.AttachedDisk;
import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.NetworkInterface;
import com.google.cloud.compute.v1.Tags;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VmAddress;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultInstanceProvider implements InstanceProvider {
    private final MachineTypeProvider machineTypeProvider;

    public Instance createInstance(VmAddress vmAddress, VMSpecs vmSpecs, AttachedDisk disk,
            NetworkInterface networkInterface) {
        var machineType = machineTypeProvider.constructMachineType(vmAddress.zone(), vmSpecs.machineType());

        return Instance.newBuilder()
                .setName(vmAddress.vmName())
                .setMachineType(machineType)
                .addDisks(disk)
                .setTags(createTags())
                .addNetworkInterfaces(networkInterface)
                .build();
    }

    private Tags createTags() {
        return Tags.newBuilder()
                .addItems("http-server")
                .addItems("https-server")
                .build();
    }
}
