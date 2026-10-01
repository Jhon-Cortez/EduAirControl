package com.eduaircontrol.backend.modules.classrooms.readmodel;

import com.eduaircontrol.backend.modules.classrooms.application.CampusService;
import com.eduaircontrol.backend.modules.classrooms.application.Codes;
import com.eduaircontrol.backend.modules.classrooms.application.EducationalEnvironmentService;
import com.eduaircontrol.backend.modules.classrooms.application.EnvironmentTypeService;
import com.eduaircontrol.backend.modules.classrooms.application.port.CampusRepository;
import com.eduaircontrol.backend.modules.classrooms.application.port.EducationalEnvironmentRepository;
import com.eduaircontrol.backend.modules.classrooms.domain.model.Campus;
import com.eduaircontrol.backend.modules.classrooms.domain.model.EducationalEnvironment;
import com.eduaircontrol.backend.modules.classrooms.domain.model.EnvironmentType;
import com.eduaircontrol.backend.modules.classrooms.domain.model.RecordStatus;
import com.eduaircontrol.backend.shared.contract.EnvironmentLookupPort;
import com.eduaircontrol.backend.shared.contract.InstallationPort;
import com.eduaircontrol.backend.shared.contract.ThresholdPort;
import com.eduaircontrol.backend.shared.contract.VariableCatalogPort;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Altas/ediciones/bajas de ambientes desde el formulario del frontend.
 * Resuelve campus y tipo por nombre (creandolos si no existen) y persiste el
 * rango de temperatura del formulario como umbral WARNING del ambiente.
 */
@Service
@RequiredArgsConstructor
public class EnvironmentWriteService {

    private final EducationalEnvironmentService environmentService;
    private final CampusService campusService;
    private final EnvironmentTypeService environmentTypeService;
    private final CampusRepository campusRepository;
    private final EducationalEnvironmentRepository environmentRepository;
    private final EnvironmentLookupPort environmentLookupPort;
    private final ThresholdPort thresholdPort;
    private final InstallationPort installationPort;
    private final VariableCatalogPort variableCatalogPort;
    private final EnvironmentReadService environmentReadService;

    @Transactional
    public EnvironmentView create(EnvironmentFormRequest request, String email) {
        Campus campus = resolveCampus(request);
        EnvironmentType type = resolveType(request);
        String code = Codes.unique(Codes.slug(request.getName()),
                candidate -> environmentRepository.existsByCampusAndCode(campus.getId(), candidate));

        EducationalEnvironment environment = environmentService.create(
                campus.getId(), code, request.getName(), type.getId(),
                parseFloor(request.getFloor()), null, parseCapacity(request.getCapacity()),
                RecordStatus.ACTIVE);

        applyTemperatureRange(environment, type, request);
        return environmentReadService.get(email, environment.getId());
    }

    @Transactional
    public EnvironmentView update(UUID environmentId, EnvironmentFormRequest request, String email) {
        EducationalEnvironment existing = environmentService.get(environmentId);
        Campus campus = resolveCampus(request);
        EnvironmentType type = resolveType(request);

        EducationalEnvironment environment = environmentService.update(
                environmentId,
                campus.getId(),
                null,
                request.getName() != null && !request.getName().isBlank() ? request.getName() : null,
                type.getId(),
                request.getFloor() != null ? parseFloor(request.getFloor()) : null,
                null,
                request.getCapacity() != null ? parseCapacity(request.getCapacity()) : null,
                null);

        applyTemperatureRange(environment, type, request);
        return environmentReadService.get(email, environmentId);
    }

    @Transactional
    public void delete(UUID environmentId) {
        environmentService.delete(environmentId);
        thresholdPort.deleteByEnvironment(environmentId);
        installationPort.retireByEnvironment(environmentId);
    }

    private void applyTemperatureRange(EducationalEnvironment environment, EnvironmentType type,
                                       EnvironmentFormRequest request) {
        if (request.getTempMin() == null && request.getTempMax() == null) {
            return;
        }
        UUID temperatureId = variableCatalogPort.findByCode("temperature")
                .map(VariableCatalogPort.VariableRef::id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Variable temperature no sembrada"));
        thresholdPort.saveWarningRange(environment.getId(), type.getId(), temperatureId,
                request.getTempMin(), request.getTempMax());
    }

    private Campus resolveCampus(EnvironmentFormRequest request) {
        if (request.getCampusId() != null) {
            return campusService.get(request.getCampusId());
        }
        if (request.getLocation() != null && !request.getLocation().isBlank()) {
            return campusService.findOrCreateByName(request.getLocation());
        }
        return campusService.findOrCreateByName(request.getName());
    }

    private EnvironmentType resolveType(EnvironmentFormRequest request) {
        if (request.getEnvironmentTypeId() != null) {
            return environmentTypeService.get(request.getEnvironmentTypeId());
        }
        return environmentTypeService.findOrCreateByName(
                request.getEnvType() != null && !request.getEnvType().isBlank()
                        ? request.getEnvType()
                        : "Aula");
    }

    private Integer parseFloor(String floor) {
        if (floor == null || floor.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(floor.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Integer parseCapacity(BigDecimal capacity) {
        return capacity == null ? null : capacity.intValue();
    }
}
