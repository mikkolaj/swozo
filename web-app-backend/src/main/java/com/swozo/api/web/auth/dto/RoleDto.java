package com.swozo.api.web.auth.dto;


import com.swozo.persistence.user.Role;

public enum RoleDto {
    STUDENT,
    TEACHER,
    TECHNICAL_TEACHER,
    ADMIN;

    public static RoleDto from(Role role) {
        return switch (role.getName().toUpperCase()) {
            case "STUDENT" -> STUDENT;
            case "TEACHER" -> TEACHER;
            case "TECHNICAL_TEACHER" -> TECHNICAL_TEACHER;
            case "ADMIN" -> ADMIN;
            default -> throw new IllegalArgumentException("Invalid App role: " + role);
        };
    }
}
