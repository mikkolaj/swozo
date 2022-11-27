package com.swozo.model.scheduling;

import com.swozo.model.scheduling.properties.IsolationMode;
import com.swozo.utils.SupportedLanguage;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record ServiceConfig(
        @Schema(required = true) String displayName,
        @Schema(required = true) String serviceName,
        @Schema(required = true) List<ParameterDescription> parameterDescriptions,
        @Schema(required = true) Set<IsolationMode> isolationModes,
        @Schema(required = true) Map<SupportedLanguage, String> configurationInstructionHtml,
        @Schema(required = true) Map<SupportedLanguage, String> usageInstructionHtml,
        @Schema(required = true) Integer provisioningSeconds
) {
}
