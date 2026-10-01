package com.eduaircontrol.backend.modules.classrooms.web.dto;

import com.eduaircontrol.backend.modules.classrooms.domain.model.RecordStatus;
import jakarta.validation.constraints.NotBlank;

public record CampusCreateRequest(
        @NotBlank(message = "code is required") String code,
        @NotBlank(message = "name is required") String name,
        String city,
        RecordStatus status) {
}
