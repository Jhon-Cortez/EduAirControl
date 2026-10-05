package com.eduaircontrol.backend.modules.analysis.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Hecho de dominio: los datos ambientales de un ambiente fueron analizados para un
 * periodo. Lo consume el dashboard ambiental.
 *
 * <p>El payload sigue el catalogo de eventos del dominio; mientras el sistema sea
 * monolithico se publica como evento interno de aplicacion.
 */
public record EnvironmentalDataAnalyzed(
        UUID eventId,
        UUID analysisId,
        UUID environmentId,
        String period,
        Instant periodStart,
        Instant periodEnd,
        Instant occurredAt,
        List<VariableSummary> results) {

    public record VariableSummary(String variableCode,
                                  BigDecimal minValue,
                                  BigDecimal maxValue,
                                  BigDecimal avgValue,
                                  int sampleCount,
                                  int exceedanceCount) {
    }

    public EnvironmentalDataAnalyzed {
        results = results == null ? List.of() : List.copyOf(results);
    }
}