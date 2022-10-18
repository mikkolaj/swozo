package com.swozo.api.web.servicemodule.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ServiceModuleInstructionDto(
    @Schema(required = true) String untrustedPossiblyDangerousHtml
) {
}
