package com.eduaircontrol.backend.modules.classrooms.web.dto;

import com.eduaircontrol.backend.modules.classrooms.domain.model.RecordStatus;

public record CampusUpdateRequest(
        String code,
        String name,
        String city,
        RecordStatus status) {
}
