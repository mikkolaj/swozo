package com.swozo.api.web.servicemodule.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

public record ServiceModuleMdaDto(
        @Schema(required = true) Boolean isIsolated,
        @Schema(required = true) Integer baseVcpu,
        @Schema(required = true) Integer baseRam,
        @Schema(required = true) Integer baseDisk,
        @Schema(required = true) Integer baseBandwidth,
        Optional<SharedServiceModuleMdaDto> sharedServiceModuleMdaDto
) {
}
