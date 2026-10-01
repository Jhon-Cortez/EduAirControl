package com.eduaircontrol.backend.modules.sensors.web;

import com.eduaircontrol.backend.modules.sensors.application.SensorCommandService;
import com.eduaircontrol.backend.modules.sensors.application.SensorQueryService;
import com.eduaircontrol.backend.modules.sensors.dto.SensorRowResponse;
import com.eduaircontrol.backend.modules.sensors.dto.SensorSaveRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final SensorQueryService sensorQueryService;
    private final SensorCommandService sensorCommandService;

    @GetMapping
    public List<SensorRowResponse> list(
            @RequestParam(name = "environmentId", required = false) UUID environmentId) {
        return sensorQueryService.list(environmentId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SensorRowResponse> create(@Valid @RequestBody SensorSaveRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sensorCommandService.create(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public SensorRowResponse update(@PathVariable String id, @Valid @RequestBody SensorSaveRequest request) {
        return sensorCommandService.update(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        sensorCommandService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Sensor dado de baja"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/toggle")
    public Map<String, Object> toggle(@PathVariable String id) {
        sensorCommandService.toggle(id);
        return Map.of("id", id, "toggled", true);
    }
}
