package com.eduaircontrol.backend.modules.analysis.infrastructure.web.dto;

import com.eduaircontrol.backend.modules.analysis.domain.model.AnalysisPeriod;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Cuerpo de POST /api/v1/analyses segun el contrato OpenAPI.
 *
 * @param environmentId ambiente educativo a analizar
 * @param period        granularidad del periodo
 * @param referenceDate ancla opcional del periodo en formato YYYY-MM-DD (UTC)
 */
public record AnalysisRequest(
        @NotNull(message = "environmentId is required") UUID environmentId,
        @NotNull(message = "period is required") AnalysisPeriod period,
        LocalDate referenceDate) {

    public Instant referenceInstant() {
        return referenceDate == null ? null : referenceDate.atStartOfDay(java.time.ZoneOffset.UTC).toInstant();
    }
}