package com.swozo.api.web.mda.vm.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record VmDto(
        @Schema(required = true) Long id,
        @Schema(required = true) String name,
        @Schema(required = true) Integer vcpu,
        @Schema(required = true) Integer ramGB,
        @Schema(required = true) Integer bandwidthMbps
) {
}
