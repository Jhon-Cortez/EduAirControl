package com.eduaircontrol.backend.modules.classrooms.web.dto;

import java.util.List;

public record ErrorResponse(String error, String message, List<Details> details, String traceId) {

    public record Details(String field, String message) {
    }

    public static ErrorResponse of(String error, String message) {
        return new ErrorResponse(error, message, null, null);
    }
}
