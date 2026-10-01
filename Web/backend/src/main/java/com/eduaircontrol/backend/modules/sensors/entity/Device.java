package com.eduaircontrol.backend.modules.sensors.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "device", schema = "sensors",
        indexes = @Index(name = "idx_device_environment", columnList = "educational_environment_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    @Id
    @Column(name = "device_id")
    private UUID id;

    @Column(name = "mac_address", nullable = false, unique = true, length = 17)
    private String macAddress;

    @Column(length = 100)
    private String name;

    @Column(name = "device_type", nullable = false, length = 50)
    private String deviceType;

    @Column(name = "firmware_version", length = 20)
    private String firmwareVersion;

    @Column(length = 32)
    private String ssid;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "api_key_prefix", unique = true, length = 12)
    private String apiKeyPrefix;

    /** Solo hash BCrypt; la clave en claro se devuelve una unica vez al generarla. */
    @Column(name = "api_key_hash", length = 100)
    private String apiKeyHash;

    @Column(name = "educational_environment_id")
    private UUID educationalEnvironmentId;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
