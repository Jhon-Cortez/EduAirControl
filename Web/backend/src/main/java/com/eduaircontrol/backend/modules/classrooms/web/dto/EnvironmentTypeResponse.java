package com.eduaircontrol.backend.modules.classrooms.web.dto;

import com.eduaircontrol.backend.modules.classrooms.domain.model.EnvironmentType;
import java.time.Instant;
import java.util.UUID;

public record EnvironmentTypeResponse(
        UUID environmentTypeId,
        String code,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt) {

    public static EnvironmentTypeResponse from(EnvironmentType type) {
        return new EnvironmentTypeResponse(
                type.getId(),
                type.getCode(),
                type.getName(),
                type.getDescription(),
                type.getCreatedAt(),
                type.getUpdatedAt());
    }
}
