package com.eduaircontrol.backend.modules.device.controller;

import com.eduaircontrol.backend.modules.device.dto.request.DeviceRequest;
import com.eduaircontrol.backend.modules.device.dto.response.DeviceResponse;
import com.eduaircontrol.backend.modules.device.service.implement.DeviceService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DeviceResponse> crear(@Valid @RequestBody DeviceRequest request) {
        DeviceResponse response = deviceService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<DeviceResponse>> listarTodos() {
        return ResponseEntity.ok(deviceService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.obtenerPorId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponse> actualizar(@PathVariable Long id,
                                                     @Valid @RequestBody DeviceRequest request) {
        return ResponseEntity.ok(deviceService.actualizar(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        deviceService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
