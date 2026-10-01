package com.eduaircontrol.backend.shared.contract;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Escritura de mediciones (modulo monitoring). Permite que la ingesta del
 * dispositivo registre lecturas sin tocar los repositorios de monitoring.
 */
public interface MeasurementWritePort {

    /** Registra una lectura; devuelve false si la muestra ya existia (idempotente). */
    boolean record(UUID sensorInstallationId, UUID variableId, BigDecimal value, Instant measuredAt);
}
