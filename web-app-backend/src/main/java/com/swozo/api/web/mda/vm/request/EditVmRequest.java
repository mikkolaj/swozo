package com.swozo.api.web.mda.vm.request;

import java.util.Optional;

public record EditVmRequest(
        Optional<String> name,
        Optional<Integer> vCpu,
        Optional<Integer> ramGB,
        Optional<Integer> imageDiskSizeGB,
        Optional<Integer> bandwidthMbps
){
}
