package com.swozo.api.web.servicemodule.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SharedServiceModuleMdaDto(
        @Schema(required = true) Integer usersPerAdditionalCore,
        @Schema(required = true) Integer usersPerAdditionalRamGb,
        @Schema(required = true) Integer usersPerAdditionalDiskGb,
        @Schema(required = true) Integer usersPerAdditionalBandwidthGbps
){
}
