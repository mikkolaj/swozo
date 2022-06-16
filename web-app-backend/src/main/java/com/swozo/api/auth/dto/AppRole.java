package com.swozo.api.auth.dto;


import com.swozo.databasemodel.users.Role;

public enum AppRole {
    STUDENT,
    TEACHER,
    TECHNICAL_TEACHER,
    ADMIN;

    public static AppRole from(Role role) {
        return switch (role.getName().toUpperCase()) {
            case "STUDENT" -> STUDENT;
            case "TEACHER" -> TEACHER;
            case "TECHNICAL_TEACHER" -> TECHNICAL_TEACHER;
            case "ADMIN" -> ADMIN;
            default -> throw new IllegalArgumentException("Invalid App role: " + role);
        };
    }
}
