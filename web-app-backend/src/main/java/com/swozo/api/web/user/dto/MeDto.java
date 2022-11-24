package com.swozo.api.web.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MeDto (
        @Schema(required = true) String name,
        @Schema(required = true) String surname,
        @Schema(required = true) String email,
        @Schema(required = true) List<FavouriteFileDto> favouriteFiles
){
}
