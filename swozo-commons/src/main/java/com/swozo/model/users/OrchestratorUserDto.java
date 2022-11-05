package com.swozo.model.users;

public record OrchestratorUserDto(
        Long id,
        String name,
        String surname,
        ActivityRole role
){
}
