package com.swozo.api.web.user.dto;

import com.swozo.api.common.files.dto.FileDto;
import com.swozo.api.web.activity.dto.ActivitySummaryDto;
import io.swagger.v3.oas.annotations.media.Schema;

public record FavouriteFileDto(
        @Schema(required = true) FileDto file,
        @Schema(required = true) ActivitySummaryDto activitySummaryDto
) {
}
