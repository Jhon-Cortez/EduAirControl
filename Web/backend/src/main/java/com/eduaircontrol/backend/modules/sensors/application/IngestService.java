package com.eduaircontrol.backend.modules.sensors.application;

import com.eduaircontrol.backend.modules.sensors.dto.IngestRequest;
import com.eduaircontrol.backend.modules.sensors.entity.Device;
import com.eduaircontrol.backend.modules.sensors.entity.Sensor;
import com.eduaircontrol.backend.modules.sensors.repository.DeviceRepository;
import com.eduaircontrol.backend.modules.sensors.repository.SensorRepository;
import com.eduaircontrol.backend.shared.contract.InstallationPort;
import com.eduaircontrol.backend.shared.contract.MeasurementWritePort;
import com.eduaircontrol.backend.shared.contract.VariableCatalogPort;
import com.eduaircontrol.backend.shared.outbox.EnvironmentalDataRecorded;
import com.eduaircontrol.backend.shared.outbox.OutboxWriter;
import com.eduaircontrol.backend.shared.security.DeviceApiKeyFilter;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class IngestService {

    private final DeviceRepository deviceRepository;
    private final SensorRepository sensorRepository;
    private final VariableCatalogPort variableCatalogPort;
    private final InstallationPort installationPort;
    private final MeasurementWritePort measurementWritePort;
    private final OutboxWriter outboxWriter;

    /**
     * Publicar el evento hacia ms-environment-monitoring es infraestructura opcional. En los
     * tests de integracion del monolito no hay broker, y fallar la ingesta porque
     * no hay RabbitMQ seria un acoplamiento inaceptable.
     */
    @Value("${app.outbox.enabled:true}")
    private boolean outboxEnabled;

    @Transactional
    public IngestResponse ingest(IngestRequest request) {
        Device device = authenticatedDevice();
        UUID environmentId = device.getEducationalEnvironmentId();
        if (environmentId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El dispositivo no tiene ambiente asignado");
        }
        if (request.getSerial() != null && !request.getSerial().isBlank()) {
            verifySerialBelongsToEnvironment(request.getSerial().trim(), environmentId);
        }

        int accepted = 0;
        int skipped = 0;
        Map<String, Integer> perVariable = new HashMap<>();
        UUID ingestCorrelationId = UUID.randomUUID();
        Instant defaultMeasuredAt = request.getMeasuredAt() != null
                ? request.getMeasuredAt()
                : Instant.now();
        // El sobre canonico representa una instantanea, no las lecturas sueltas. Por
        // eso se agrupan los valores aceptados por instante medido.
        Map<Instant, Map<String, BigDecimal>> snapshots = new TreeMap<>();

        for (IngestRequest.Reading reading : request.getReadings()) {
            VariableCatalogPort.VariableRef variable = variableCatalogPort.findByCode(reading.getVariable())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Variable desconocida: " + reading.getVariable()));
            UUID installationId = installationPort.activeInstallationId(environmentId, variable.id())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                            "No hay sensor instalado para " + reading.getVariable() + " en este ambiente"));
            Instant measuredAt = reading.getMeasuredAt() != null ? reading.getMeasuredAt()
                    : defaultMeasuredAt;
            boolean saved = measurementWritePort.record(installationId, variable.id(),
                    reading.getValue(), measuredAt);
            if (saved) {
                accepted++;
                perVariable.merge(variable.code(), 1, Integer::sum);
                snapshots.computeIfAbsent(measuredAt, instant -> new HashMap<>())
                        .put(variable.code(), reading.getValue());
            } else {
                skipped++;
            }
        }

        // El outbox se encola dentro de esta misma transaccion: si el commit falla,
        // no queda ni la medicion ni el evento. Es el punto del patron. Solo se
        // publican los valores aceptados y se conserva su instante medido.
        if (outboxEnabled && !snapshots.isEmpty()) {
            snapshots.forEach((measuredAt, values) -> {
                EnvironmentalDataRecorded event = EnvironmentalDataRecorded.at(
                        environmentId,
                        measuredAt,
                        values.get("temperature"),
                        values.get("humidity"),
                        values.get("co2"),
                        values.get("noise"),
                        ingestCorrelationId);
                outboxWriter.append(
                        event.eventType(),
                        event.aggregateType(),
                        event.aggregateId().toString(),
                        EnvironmentalDataRecorded.ROUTING_KEY,
                        event,
                        event.occurredAt());
            });
        }

        device.setLastSeenAt(Instant.now());
        device.setStatus("conectado");
        device.setUpdatedAt(Instant.now());
        deviceRepository.save(device);

        return new IngestResponse(environmentId, accepted, skipped, perVariable);
    }

    private Device authenticatedDevice() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Device device)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "X-API-Key requerida");
        }
        return device;
    }

    private void verifySerialBelongsToEnvironment(String serial, UUID environmentId) {
        Sensor sensor = sensorRepository.findBySerialNumber(serial)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sensor desconocido: " + serial));
        UUID owner = installationPort.installedEnvironmentOf(sensor.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "El sensor no esta instalado"));
        if (!owner.equals(environmentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El sensor no pertenece al ambiente del dispositivo");
        }
    }

    public record IngestResponse(UUID environmentId, int accepted, int skipped,
                                 Map<String, Integer> perVariable) {
    }
}
