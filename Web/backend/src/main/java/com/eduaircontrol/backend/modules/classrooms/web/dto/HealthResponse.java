package com.eduaircontrol.backend.modules.classrooms.web.dto;

import java.time.Instant;

public record HealthResponse(String status, Instant timestamp, Dependencies dependencies) {

    public record Dependencies(String database) {
    }

    public static HealthResponse healthy() {
        return new HealthResponse("ok", Instant.now(), new Dependencies("ok"));
    }

    public static HealthResponse databaseDown() {
        return new HealthResponse("down", Instant.now(), new Dependencies("down"));
    }
}
