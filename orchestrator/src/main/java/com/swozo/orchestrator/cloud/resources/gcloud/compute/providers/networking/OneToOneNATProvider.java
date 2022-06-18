package com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.networking;

import com.google.cloud.compute.v1.AccessConfig;
import com.google.cloud.compute.v1.NetworkInterface;

public class OneToOneNATProvider implements NetworkInterfaceProvider {
    private static final String NETWORK_TYPE = "ONE_TO_ONE_NAT";
    private static final String DEFAULT_ACCESS_CONFIG_NAME = "External NAT";

    @Override
    public NetworkInterface createNetworkInterface(String networkName) {
        var networkAccessConfig = AccessConfig.newBuilder()
                .setType(NETWORK_TYPE)
                .setName(DEFAULT_ACCESS_CONFIG_NAME)
                .build();

        return NetworkInterface.newBuilder()
                .setName(networkName)
                .addAccessConfigs(networkAccessConfig)
                .build();
    }
}
