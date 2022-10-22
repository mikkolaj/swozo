package com.swozo.model.links;

import com.swozo.utils.SupportedLanguage;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public record ActivityLinkInfo(
        @Schema(required = true) String url,
        @Schema(required = true) String connectionInfo,
        @Schema(required = true) Map<SupportedLanguage, String> connectionInstructionHtml
) {
}
