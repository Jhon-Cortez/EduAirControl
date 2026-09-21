package com.eduaircontrol.backend.modules.aula.controller;

import com.eduaircontrol.backend.modules.aula.dto.request.AulaRequest;
import com.eduaircontrol.backend.modules.aula.dto.response.AulaResponse;
import com.eduaircontrol.backend.modules.aula.service.implement.AulaService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aulas")
@RequiredArgsConstructor
public class AulaController {

    private final AulaService aulaService;

    @PostMapping
    public ResponseEntity<AulaResponse> crear(@Valid @RequestBody AulaRequest request) {
        AulaResponse response = aulaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AulaResponse>> listarTodas() {
        return ResponseEntity.ok(aulaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AulaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(aulaService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AulaResponse> actualizar(@PathVariable Long id,
                                                   @Valid @RequestBody AulaRequest request) {
        return ResponseEntity.ok(aulaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        aulaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
