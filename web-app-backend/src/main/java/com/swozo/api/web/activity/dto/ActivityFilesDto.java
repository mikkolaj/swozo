package com.swozo.api.web.activity.dto;

import com.swozo.api.common.files.dto.FileDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

public record ActivityFilesDto(
        @Schema(required = true) Map<Long, List<FileDto>> activityModuleIdToUserFiles
) {
}
