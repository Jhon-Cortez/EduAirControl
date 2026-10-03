package com.eduaircontrol.backend.modules.sensors.web;

import com.eduaircontrol.backend.modules.sensors.application.DeviceService;
import com.eduaircontrol.backend.modules.sensors.dto.DeviceCreateRequest;
import com.eduaircontrol.backend.modules.sensors.dto.DeviceResponse;
import com.eduaircontrol.backend.modules.sensors.dto.DeviceUpdateRequest;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public List<DeviceResponse> list() {
        return deviceService.list();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DeviceResponse> create(@Valid @RequestBody DeviceCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deviceService.create(request));
    }

    /** Genera una clave nueva; la anterior deja de validar. La respuesta la trae una sola vez. */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{macAddress}/rotate-key")
    public DeviceResponse rotateKey(@PathVariable String macAddress) {
        return deviceService.rotateKey(macAddress);
    }

    /** Edicion parcial: los nulos conservan el valor actual. */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public DeviceResponse update(@PathVariable UUID id,
                                 @Valid @RequestBody DeviceUpdateRequest request) {
        return deviceService.update(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable UUID id) {
        deviceService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Dispositivo eliminado"));
    }
}
