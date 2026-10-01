package com.eduaircontrol.backend.modules.classrooms.web.dto;

public record EnvironmentTypeUpdateRequest(
        String code,
        String name,
        String description) {
}
