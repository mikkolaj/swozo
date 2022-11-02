package com.swozo.api.web.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SendResetPasswordEmailRequest(
        @Schema(required = true) String email
){
}
