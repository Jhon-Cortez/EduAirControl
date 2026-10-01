package com.eduaircontrol.backend.shared.contract;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato de lectura del catalogo de ambientes (modulo classrooms).
 * Se declara aqui para que monitoring/ux puedan resolver ambientes sin
 * depender de los repositorios de classrooms.
 */
public interface EnvironmentLookupPort {

    Optional<EnvironmentInfo> findById(UUID environmentId);

    List<EnvironmentInfo> findAll();

    Optional<EnvironmentInfo> findByCode(String code);

    record EnvironmentInfo(
            UUID id,
            String code,
            String name,
            UUID campusId,
            UUID environmentTypeId,
            Integer floor,
            Integer occupancyCapacity,
            String status) {
    }
}
