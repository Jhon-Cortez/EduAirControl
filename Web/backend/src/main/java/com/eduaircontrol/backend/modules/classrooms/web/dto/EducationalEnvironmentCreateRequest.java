package com.eduaircontrol.backend.modules.classrooms.web.dto;

import com.eduaircontrol.backend.modules.classrooms.domain.model.RecordStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record EducationalEnvironmentCreateRequest(
        @NotNull(message = "campusId is required") UUID campusId,
        @NotBlank(message = "code is required") String code,
        @NotBlank(message = "name is required")
        @Size(max = 120, message = "name must be at most 120 characters") String name,
        @NotNull(message = "environmentTypeId is required") UUID environmentTypeId,
        @Min(value = 0, message = "floor must be 0 or greater") Integer floor,
        @DecimalMin(value = "0.01", message = "areaM2 must be greater than 0") BigDecimal areaM2,
        @Min(value = 1, message = "occupancyCapacity must be 1 or greater") Integer occupancyCapacity,
        RecordStatus status) {
}
