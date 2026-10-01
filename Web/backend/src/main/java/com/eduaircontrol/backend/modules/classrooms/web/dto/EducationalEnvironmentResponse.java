package com.eduaircontrol.backend.modules.classrooms.web.dto;

import com.eduaircontrol.backend.modules.classrooms.domain.model.EducationalEnvironment;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record EducationalEnvironmentResponse(
        UUID educationalEnvironmentId,
        UUID campusId,
        String code,
        String name,
        UUID environmentTypeId,
        Integer floor,
        BigDecimal areaM2,
        Integer occupancyCapacity,
        String status,
        Instant createdAt,
        Instant updatedAt) {

    public static EducationalEnvironmentResponse from(EducationalEnvironment environment) {
        return new EducationalEnvironmentResponse(
                environment.getId(),
                environment.getCampusId(),
                environment.getCode(),
                environment.getName(),
                environment.getEnvironmentTypeId(),
                environment.getFloor(),
                environment.getAreaM2(),
                environment.getOccupancyCapacity(),
                environment.getStatus().name(),
                environment.getCreatedAt(),
                environment.getUpdatedAt());
    }
}
