package com.swozo.dto.activitymodule;

import com.swozo.dto.activity.ActivityDetailsReq;
import com.swozo.dto.servicemodule.ServiceModuleDetailsReq;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ActivityModuleDetailsReq(
        @Schema(required = true) ServiceModuleDetailsReq module,
        @Schema(required = true) ActivityDetailsReq activity,
        @Schema(required = true) String instruction,
        @Schema(required = true) List<String> links
) {
}
