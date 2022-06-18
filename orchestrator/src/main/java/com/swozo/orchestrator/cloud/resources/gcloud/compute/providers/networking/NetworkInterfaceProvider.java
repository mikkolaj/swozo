package com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.networking;

import com.google.cloud.compute.v1.NetworkInterface;

public interface NetworkInterfaceProvider {
    NetworkInterface createNetworkInterface(String networkName);
}
