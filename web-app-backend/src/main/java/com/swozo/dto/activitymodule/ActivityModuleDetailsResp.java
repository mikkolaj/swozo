package com.swozo.dto.activitymodule;

import com.swozo.dto.activity.ActivityDetailsResp;
import com.swozo.dto.servicemodule.ServiceModuleDetailsResp;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ActivityModuleDetailsResp(
        @Schema(required = true) Long id,
        @Schema(required = true) ServiceModuleDetailsResp module,
        @Schema(required = true) ActivityDetailsResp activity,
        @Schema(required = true) String instruction,
        @Schema(required = true) List<String> links
) {
}
