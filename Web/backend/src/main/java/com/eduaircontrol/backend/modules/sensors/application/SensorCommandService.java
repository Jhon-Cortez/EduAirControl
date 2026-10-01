package com.eduaircontrol.backend.modules.sensors.application;

import com.eduaircontrol.backend.modules.sensors.dto.SensorRowResponse;
import com.eduaircontrol.backend.modules.sensors.dto.SensorSaveRequest;
import com.eduaircontrol.backend.modules.sensors.entity.Sensor;
import com.eduaircontrol.backend.modules.sensors.entity.SensorInstallation;
import com.eduaircontrol.backend.modules.sensors.entity.SensorModel;
import com.eduaircontrol.backend.modules.sensors.entity.SensorStatus;
import com.eduaircontrol.backend.modules.sensors.entity.SensorVariable;
import com.eduaircontrol.backend.modules.sensors.repository.SensorInstallationRepository;
import com.eduaircontrol.backend.modules.sensors.repository.SensorModelRepository;
import com.eduaircontrol.backend.modules.sensors.repository.SensorRepository;
import com.eduaircontrol.backend.modules.sensors.repository.SensorStatusRepository;
import com.eduaircontrol.backend.modules.sensors.repository.SensorVariableRepository;
import com.eduaircontrol.backend.shared.contract.EnvironmentLookupPort;
import com.eduaircontrol.backend.shared.contract.ThresholdPort;
import com.eduaircontrol.backend.shared.contract.VariableCatalogPort;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SensorCommandService {

    private static final Map<String, String> MODEL_BY_VARIABLE = Map.of(
            "temperature", "DHT22",
            "humidity", "DHT22",
            "co2", "SCD41",
            "noise", "KY038");

    private final SensorRepository sensorRepository;
    private final SensorInstallationRepository installationRepository;
    private final SensorVariableRepository sensorVariableRepository;
    private final SensorModelRepository modelRepository;
    private final SensorStatusRepository statusRepository;
    private final EnvironmentLookupPort environmentLookupPort;
    private final VariableCatalogPort variableCatalogPort;
    private final ThresholdPort thresholdPort;

    @Transactional
    public SensorRowResponse create(SensorSaveRequest request) {
        EnvironmentLookupPort.EnvironmentInfo environment = requireEnvironment(request.getEnvironmentId());
        VariableCatalogPort.VariableRef variable = requireVariable(request.getVariable());
        Sensor sensor = createSensor(environment, variable, request.getId());

        install(sensor, environment);
        bindVariable(sensor, variable);
        applyRange(environment, variable, request);

        return row(sensor, environment, variable, request, "active", "Ahora");
    }

    @Transactional
    public SensorRowResponse update(String serial, SensorSaveRequest request) {
        Sensor sensor = requireSensor(serial);
        EnvironmentLookupPort.EnvironmentInfo environment = requireEnvironment(request.getEnvironmentId());
        VariableCatalogPort.VariableRef variable = requireVariable(request.getVariable());

        installationRepository.findBySensorIdAndRemovedAtIsNull(sensor.getId())
                .ifPresent(existing -> {
                    if (!existing.getEducationalEnvironmentId().equals(environment.id())) {
                        existing.setRemovedAt(Instant.now());
                        installationRepository.save(existing);
                        install(sensor, environment);
                    }
                });
        if (installationRepository.findBySensorIdAndRemovedAtIsNull(sensor.getId()).isEmpty()) {
            install(sensor, environment);
        }

        bindVariable(sensor, variable);
        applyRange(environment, variable, request);

        boolean active = !"offline".equals(request.getStatus());
        setInstalled(sensor, environment, active);

        sensor.setUpdatedAt(Instant.now());
        sensorRepository.save(sensor);

        return row(sensor, environment, variable, request, active ? "active" : "offline",
                active ? "Ahora" : "Sin conexión");
    }

    @Transactional
    public void delete(String serial) {
        Sensor sensor = requireSensor(serial);
        retire(sensor);
        sensorVariableRepository.deleteBySensorId(sensor.getId());
    }

    @Transactional
    public SensorRowResponse toggle(String serial) {
        Sensor sensor = requireSensor(serial);
        SensorInstallation installation = installationRepository
                .findBySensorIdAndRemovedAtIsNull(sensor.getId()).orElse(null);

        if (installation != null) {
            installation.setRemovedAt(Instant.now());
            installationRepository.save(installation);
            sensor.setUpdatedAt(Instant.now());
            sensorRepository.save(sensor);
            return null;
        }

        UUID environmentId = installationRepository.findBySensorId(sensor.getId()).stream()
                .filter(candidate -> candidate.getRemovedAt() == null)
                .findFirst()
                .map(SensorInstallation::getEducationalEnvironmentId)
                .orElse(null);
        if (environmentId == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El sensor no tiene historial de instalación; edítalo para reinstalarlo");
        }
        EnvironmentLookupPort.EnvironmentInfo environment = requireEnvironment(environmentId);
        install(sensor, environment);
        sensor.setUpdatedAt(Instant.now());
        sensorRepository.save(sensor);
        return null;
    }

    private void install(Sensor sensor, EnvironmentLookupPort.EnvironmentInfo environment) {
        installationRepository.save(SensorInstallation.builder()
                .id(UUID.randomUUID())
                .sensorId(sensor.getId())
                .educationalEnvironmentId(environment.id())
                .installedAt(Instant.now())
                .build());
    }

    private void retire(Sensor sensor) {
        installationRepository.findBySensorIdAndRemovedAtIsNull(sensor.getId())
                .ifPresent(installation -> {
                    installation.setRemovedAt(Instant.now());
                    installationRepository.save(installation);
                });
    }

    private void setInstalled(Sensor sensor, EnvironmentLookupPort.EnvironmentInfo environment, boolean active) {
        boolean installed = installationRepository
                .findBySensorIdAndEducationalEnvironmentIdAndRemovedAtIsNull(sensor.getId(), environment.id())
                .isPresent();
        if (active && !installed) {
            install(sensor, environment);
        } else if (!active && installed) {
            retire(sensor);
        }
    }

    private void bindVariable(Sensor sensor, VariableCatalogPort.VariableRef variable) {
        if (sensorVariableRepository.existsBySensorIdAndVariableId(sensor.getId(), variable.id())) {
            return;
        }
        sensorVariableRepository.findBySensorId(sensor.getId()).stream()
                .filter(binding -> !binding.getVariableId().equals(variable.id()))
                .toList()
                .forEach(sensorVariableRepository::delete);
        sensorVariableRepository.save(new SensorVariable(sensor.getId(), variable.id()));
    }

    private void applyRange(EnvironmentLookupPort.EnvironmentInfo environment,
                            VariableCatalogPort.VariableRef variable, SensorSaveRequest request) {
        if (request.getMin() != null || request.getMax() != null) {
            thresholdPort.saveWarningRange(environment.id(), environment.environmentTypeId(), variable.id(),
                    request.getMin(), request.getMax());
        }
    }

    private Sensor createSensor(EnvironmentLookupPort.EnvironmentInfo environment,
                                VariableCatalogPort.VariableRef variable, String requestedSerial) {
        String base = requestedSerial != null && !requestedSerial.isBlank()
                ? requestedSerial.trim().toUpperCase()
                : baseSerial(environment, variable);
        SensorModel model = modelRepository.findByCode(MODEL_BY_VARIABLE.getOrDefault(variable.code(), "DHT22"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Modelo de sensor no sembrado"));
        SensorStatus status = statusRepository.findByCode("ACTIVE")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Estado de sensor no sembrado"));

        Sensor sensor = Sensor.builder()
                .id(UUID.randomUUID())
                .serialNumber(uniqueSerial(base))
                .sensorModelId(model.getId())
                .sensorStatusId(status.getId())
                .lastSeenAt(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return sensorRepository.save(sensor);
    }

    private String baseSerial(EnvironmentLookupPort.EnvironmentInfo environment,
                              VariableCatalogPort.VariableRef variable) {
        return "EA-" + environment.code() + "-" + variable.code().substring(0, 1).toUpperCase(Locale.ROOT);
    }

    private String uniqueSerial(String base) {
        String candidate = base;
        for (int suffix = 2; suffix < 1000; suffix++) {
            if (sensorRepository.findBySerialNumber(candidate).isEmpty()) {
                return candidate;
            }
            candidate = base + "-" + suffix;
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "No se pudo generar un numero de serie unico");
    }

    private SensorRowResponse row(Sensor sensor, EnvironmentLookupPort.EnvironmentInfo environment,
                                  VariableCatalogPort.VariableRef variable, SensorSaveRequest request,
                                  String status, String lastSync) {
        return new SensorRowResponse(
                sensor.getSerialNumber(),
                sensor.getId(),
                environment.id(),
                variable.code(),
                !"offline".equals(status),
                status,
                lastSync,
                request.getMin(),
                request.getMax());
    }

    private Sensor requireSensor(String serial) {
        return sensorRepository.findBySerialNumber(serial)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor no encontrado"));
    }

    private EnvironmentLookupPort.EnvironmentInfo requireEnvironment(UUID id) {
        return environmentLookupPort.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ambiente no encontrado"));
    }

    private VariableCatalogPort.VariableRef requireVariable(String code) {
        return variableCatalogPort.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Variable desconocida: " + code));
    }
}
