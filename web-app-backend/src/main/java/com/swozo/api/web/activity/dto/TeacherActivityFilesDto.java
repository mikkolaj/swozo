package com.swozo.api.web.activity.dto;

import com.swozo.api.web.user.dto.UserDetailsDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public record TeacherActivityFilesDto(
        @Schema(required = true) Map<Long, ActivityFilesDto> userIdToUserFiles,
        @Schema(required = true) Map<Long, UserDetailsDto> userIdToUserDetails
){
}
