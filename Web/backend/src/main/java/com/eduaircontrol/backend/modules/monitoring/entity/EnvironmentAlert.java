package com.eduaircontrol.backend.modules.monitoring.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "environment_alert", schema = "monitoring")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentAlert {

    @Id
    @Column(name = "environment_alert_id")
    private UUID id;

    @Column(name = "educational_environment_id", nullable = false)
    private UUID educationalEnvironmentId;

    @Column(name = "variable_id", nullable = false)
    private UUID variableId;

    @Column(name = "variable_threshold_id")
    private UUID variableThresholdId;

    @Column(name = "triggering_measurement_id")
    private UUID triggeringMeasurementId;

    @Column(name = "alert_status_id", nullable = false)
    private UUID alertStatusId;

    @Column(name = "raised_at", nullable = false)
    private Instant raisedAt;

    @Column(name = "acknowledged_at")
    private Instant acknowledgedAt;

    @Column(name = "acknowledged_by")
    private UUID acknowledgedBy;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
