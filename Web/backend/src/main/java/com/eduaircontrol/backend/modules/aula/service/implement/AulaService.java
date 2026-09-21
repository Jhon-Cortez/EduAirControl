package com.eduaircontrol.backend.modules.aula.service.implement;

import com.eduaircontrol.backend.modules.aula.dto.request.AulaRequest;
import com.eduaircontrol.backend.modules.aula.dto.response.AulaResponse;
import com.eduaircontrol.backend.modules.aula.entity.Aula;
import com.eduaircontrol.backend.modules.aula.repository.AulaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AulaService {

    private final AulaRepository aulaRepository;

    public AulaResponse crear(AulaRequest request) {
        if (aulaRepository.existsByCodigoAula(request.getCodigoAula())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un aula con el codigo: " + request.getCodigoAula());
        }

        Aula aula = Aula.builder()
                .codigoAula(request.getCodigoAula())
                .nombre(request.getNombre())
                .ubicacion(request.getUbicacion())
                .capacidad(request.getCapacidad())
                .tipoAula(request.getTipoAula())
                .estado(request.getEstado() != null ? request.getEstado() : "activa")
                .build();

        Aula saved = aulaRepository.save(aula);
        return toResponse(saved);
    }

    public List<AulaResponse> listarTodas() {
        return aulaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AulaResponse obtenerPorId(Long id) {
        Aula aula = aulaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Aula no encontrada con id: " + id));
        return toResponse(aula);
    }

    public AulaResponse actualizar(Long id, AulaRequest request) {
        Aula aula = aulaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Aula no encontrada con id: " + id));

        if (!aula.getCodigoAula().equals(request.getCodigoAula())
                && aulaRepository.existsByCodigoAula(request.getCodigoAula())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un aula con el codigo: " + request.getCodigoAula());
        }

        aula.setCodigoAula(request.getCodigoAula());
        aula.setNombre(request.getNombre());
        aula.setUbicacion(request.getUbicacion());
        aula.setCapacidad(request.getCapacidad());
        aula.setTipoAula(request.getTipoAula());
        if (request.getEstado() != null) {
            aula.setEstado(request.getEstado());
        }

        Aula updated = aulaRepository.save(aula);
        return toResponse(updated);
    }

    public void eliminar(Long id) {
        if (!aulaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Aula no encontrada con id: " + id);
        }
        aulaRepository.deleteById(id);
    }

    private AulaResponse toResponse(Aula aula) {
        return AulaResponse.builder()
                .id(aula.getId())
                .codigoAula(aula.getCodigoAula())
                .nombre(aula.getNombre())
                .ubicacion(aula.getUbicacion())
                .capacidad(aula.getCapacidad())
                .tipoAula(aula.getTipoAula())
                .estado(aula.getEstado())
                .fechaCreacion(aula.getFechaCreacion())
                .fechaActualizacion(aula.getFechaActualizacion())
                .build();
    }
}
