package com.eduaircontrol.backend.modules.sensors.entity;

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
@Table(name = "sensor_installation", schema = "sensors")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorInstallation {

    @Id
    @Column(name = "sensor_installation_id")
    private UUID id;

    @Column(name = "sensor_id", nullable = false)
    private UUID sensorId;

    @Column(name = "educational_environment_id", nullable = false)
    private UUID educationalEnvironmentId;

    @Column(name = "installed_at", nullable = false)
    private Instant installedAt;

    @Column(name = "removed_at")
    private Instant removedAt;
}
