package com.eduaircontrol.backend.shared.outbox;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Hecho de dominio emitido cuando se aceptan lecturas ambientales.
 *
 * <p>Usa el sobre estandar de {@code 02-domain/domain-events.md}: identificadores del
 * hecho y del agregado, momento de ocurrencia, version del esquema, datos y
 * metadatos de trazabilidad. El payload conserva un campo por variable ambiental,
 * en lugar de una lista, tal como lo fija el contrato.
 */
public record EnvironmentalDataRecorded(
        UUID eventId,
        String eventType,
        UUID aggregateId,
        String aggregateType,
        Instant occurredAt,
        Integer version,
        Payload payload,
        Metadata metadata) {

    public static final String TYPE = "EnvironmentalDataRecorded";

    public static final String AGGREGATE_TYPE = "EnvironmentalData";

    public static final int SCHEMA_VERSION = 1;

    public static final String ROUTING_KEY = "environmental.data.recorded";

    public record Payload(
            UUID environmentId,
            BigDecimal temperature,
            BigDecimal humidity,
            BigDecimal co2,
            BigDecimal noiseLevel) {
    }

    public record Metadata(
            UUID correlationId,
            UUID causationId,
            UUID userId) {
    }

    public static EnvironmentalDataRecorded at(
            UUID environmentId,
            Instant occurredAt,
            BigDecimal temperature,
            BigDecimal humidity,
            BigDecimal co2,
            BigDecimal noiseLevel,
            UUID correlationId) {
        UUID eventId = UUID.randomUUID();
        return new EnvironmentalDataRecorded(
                eventId,
                TYPE,
                environmentId,
                AGGREGATE_TYPE,
                occurredAt,
                SCHEMA_VERSION,
                new Payload(environmentId, temperature, humidity, co2, noiseLevel),
                new Metadata(correlationId, null, null));
    }

    public EnvironmentalDataRecorded {
        if (version == null || version <= 0) {
            version = SCHEMA_VERSION;
        }
    }
}
