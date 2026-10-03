package com.eduaircontrol.backend.modules.sensors.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Fila del panel de sensores: 1 sensor + su variable.
 * "id" es el numero de serie (identificador externo estable de la UI).
 * "installed" distingue el sensor retirado (se list para poder reactivarlo)
 * del sensor activo que simplemente dejo de reportar.
 */
public record SensorRowResponse(
        String id,
        UUID sensorId,
        UUID environmentId,
        String variable,
        boolean active,
        String status,
        String lastSync,
        BigDecimal min,
        BigDecimal max,
        boolean installed) {
}
