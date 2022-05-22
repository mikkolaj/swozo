package com.swozo.api.auth.dto;

public record AuthData(String accessToken, long expiresIn) {
}
