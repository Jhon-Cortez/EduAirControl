package com.eduaircontrol.backend.modules.sensors.web;

import com.eduaircontrol.backend.modules.sensors.application.IngestService;
import com.eduaircontrol.backend.modules.sensors.dto.IngestRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Entrada de telemetria de los dispositivos ESP32. La autenticacion por
 * X-API-Key ocurre en DeviceApiKeyFilter, antes de validar el payload.
 */
@RestController
@RequestMapping("/ingest/v1")
@RequiredArgsConstructor
public class IngestController {

    private final IngestService ingestService;

    @PostMapping("/measurements")
    public ResponseEntity<IngestService.IngestResponse> ingest(
            @Valid @RequestBody IngestRequest request) {
        return ResponseEntity.ok(ingestService.ingest(request));
    }
}
