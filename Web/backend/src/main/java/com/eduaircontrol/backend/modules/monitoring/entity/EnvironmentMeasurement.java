package com.eduaircontrol.backend.modules.monitoring.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "environment_measurement", schema = "monitoring")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentMeasurement {

    @Id
    @Column(name = "environment_measurement_id")
    private UUID id;

    @Column(name = "sensor_installation_id", nullable = false)
    private UUID sensorInstallationId;

    @Column(name = "variable_id", nullable = false)
    private UUID variableId;

    @Column(name = "measured_value", nullable = false)
    private BigDecimal measuredValue;

    @Column(name = "measured_at", nullable = false)
    private Instant measuredAt;

    @Column(name = "quality_flag_id", nullable = false)
    private UUID qualityFlagId;
}
