package com.eduaircontrol.backend.modules.classrooms.web;

import com.eduaircontrol.backend.modules.classrooms.web.dto.HealthResponse;
import java.sql.Connection;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final DataSource dataSource;

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                return ResponseEntity.ok(HealthResponse.healthy());
            }
        } catch (Exception e) {
            // dependency down — handled below
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(HealthResponse.databaseDown());
    }
}
