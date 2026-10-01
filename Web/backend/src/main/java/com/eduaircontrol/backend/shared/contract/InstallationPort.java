package com.eduaircontrol.backend.shared.contract;

import java.util.UUID;

/**
 * Operaciones de instalaciones de sensores que otros modulos necesitan
 * (para retirar los sensores de un ambiente dado de baja).
 */
public interface InstallationPort {

    void retireByEnvironment(UUID environmentId);

    java.util.Optional<UUID> activeInstallationId(UUID environmentId, UUID variableId);

    /** Ambiente donde el sensor tiene instalacion activa. */
    java.util.Optional<UUID> installedEnvironmentOf(UUID sensorId);
}
