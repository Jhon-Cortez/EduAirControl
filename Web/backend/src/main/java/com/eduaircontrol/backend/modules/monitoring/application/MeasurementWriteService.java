package com.eduaircontrol.backend.modules.monitoring.application;

import com.eduaircontrol.backend.modules.monitoring.entity.EnvironmentMeasurement;
import com.eduaircontrol.backend.modules.monitoring.repository.EnvironmentMeasurementRepository;
import com.eduaircontrol.backend.shared.contract.MeasurementWritePort;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MeasurementWriteService implements MeasurementWritePort {

    private final EnvironmentMeasurementRepository measurementRepository;
    private final QualityFlagService qualityFlagService;

    @Override
    @Transactional
    public boolean record(UUID sensorInstallationId, UUID variableId, BigDecimal value, Instant measuredAt) {
        Instant timestamp = measuredAt != null ? measuredAt : Instant.now();
        if (measurementRepository.existsBySensorInstallationIdAndVariableIdAndMeasuredAt(
                sensorInstallationId, variableId, timestamp)) {
            return false;
        }
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "value es obligatorio");
        }
        measurementRepository.save(EnvironmentMeasurement.builder()
                .id(UUID.randomUUID())
                .sensorInstallationId(sensorInstallationId)
                .variableId(variableId)
                .measuredValue(value)
                .measuredAt(timestamp)
                .qualityFlagId(qualityFlagService.goodFlagId())
                .build());
        return true;
    }
}
