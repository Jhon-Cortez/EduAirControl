package com.eduaircontrol.backend.modules.classrooms.web.dto;

import com.eduaircontrol.backend.modules.classrooms.domain.model.RecordStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record EducationalEnvironmentUpdateRequest(
        UUID campusId,
        String code,
        @Size(max = 120, message = "name must be at most 120 characters") String name,
        UUID environmentTypeId,
        @Min(value = 0, message = "floor must be 0 or greater") Integer floor,
        @DecimalMin(value = "0.01", message = "areaM2 must be greater than 0") BigDecimal areaM2,
        @Min(value = 1, message = "occupancyCapacity must be 1 or greater") Integer occupancyCapacity,
        RecordStatus status) {
}
