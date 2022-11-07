package com.swozo.api.web.mda.vm.request;

import java.util.Optional;

public record EdtiVmRequest (
        Optional<String> name,
        Optional<Integer> vCpu,
        Optional<Integer> ramGB,
        Optional<Integer> bandwidthMbps
){
}
