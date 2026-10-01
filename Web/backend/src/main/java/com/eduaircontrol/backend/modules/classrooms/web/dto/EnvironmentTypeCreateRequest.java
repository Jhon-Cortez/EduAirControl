package com.eduaircontrol.backend.modules.classrooms.web.dto;

import jakarta.validation.constraints.NotBlank;

public record EnvironmentTypeCreateRequest(
        @NotBlank(message = "code is required") String code,
        @NotBlank(message = "name is required") String name,
        String description) {
}
