package com.swozo.dto.activity;

import io.swagger.v3.oas.annotations.media.Schema;

public record ActivityLinkInfo(
        // return null if we don't have urls yet
        @Schema(required = false) String url,
        @Schema(required = true) String serviceName,
        @Schema(required = true) String connectionInstruction,
        @Schema(required = false) String connectionInfo
) {
}
