package com.eduaircontrol.backend.modules.classrooms.persistence;

import com.eduaircontrol.backend.modules.classrooms.domain.model.EducationalEnvironment;
import com.eduaircontrol.backend.shared.contract.EnvironmentLookupPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * Expone el catalogo de ambientes a traves del contrato compartido
 * (solo lectura; los demas modulos no tocan los repositorios de classrooms).
 */
@Repository
@RequiredArgsConstructor
public class EnvironmentLookupAdapter implements EnvironmentLookupPort {

    private final EducationalEnvironmentJpaRepository environmentRepository;

    @Override
    public Optional<EnvironmentInfo> findById(UUID environmentId) {
        if (environmentId == null) {
            return Optional.empty();
        }
        return environmentRepository.findById(environmentId)
                .filter(environment -> !environment.isDeleted())
                .map(EnvironmentLookupAdapter::toInfo);
    }

    @Override
    public List<EnvironmentInfo> findAll() {
        return environmentRepository.findAll().stream()
                .filter(environment -> !environment.isDeleted())
                .map(EnvironmentLookupAdapter::toInfo)
                .toList();
    }

    @Override
    public Optional<EnvironmentInfo> findByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        String normalized = code.trim().toUpperCase();
        return environmentRepository.findAll().stream()
                .filter(environment -> !environment.isDeleted())
                .filter(environment -> normalized.equalsIgnoreCase(environment.getCode()))
                .map(EnvironmentLookupAdapter::toInfo)
                .findFirst();
    }

    private static EnvironmentInfo toInfo(EducationalEnvironment environment) {
        return new EnvironmentInfo(
                environment.getId(),
                environment.getCode(),
                environment.getName(),
                environment.getCampusId(),
                environment.getEnvironmentTypeId(),
                environment.getFloor(),
                environment.getOccupancyCapacity(),
                environment.getStatus() != null ? environment.getStatus().name() : null);
    }
}
