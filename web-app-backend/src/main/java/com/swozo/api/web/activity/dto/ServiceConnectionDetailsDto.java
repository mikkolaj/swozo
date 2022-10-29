package com.swozo.api.web.activity.dto;

import com.swozo.model.utils.InstructionDto;
import com.swozo.utils.SupportedLanguage;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public record ServiceConnectionDetailsDto(
        @Schema(required = true) Map<SupportedLanguage, InstructionDto> connectionInstructions,
        @Schema(required = true) String url
) {
}
