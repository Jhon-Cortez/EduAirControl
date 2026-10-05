package com.eduaircontrol.backend.modules.analysis.infrastructure.web.dto;

import com.eduaircontrol.backend.modules.analysis.domain.model.AnalysisPeriod;
import com.eduaircontrol.backend.modules.analysis.domain.model.AnalysisResult;
import com.eduaircontrol.backend.modules.analysis.domain.model.AnalysisStatusCode;
import com.eduaircontrol.backend.modules.analysis.domain.model.EnvironmentalAnalysis;
import com.eduaircontrol.backend.shared.contract.VariableCatalogPort;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Respuesta de la API de analisis segun el contrato OpenAPI.
 *
 * <p>El periodo se expone como la granularidad que lo produjo. La tabla solo guarda
 * la ventana materializada, asi que el contrato no devuelve las dos cosas a la vez.
 */
public record AnalysisResponse(
        UUID id,
        UUID environmentId,
        AnalysisPeriod period,
        Instant periodStart,
        Instant periodEnd,
        AnalysisStatusCode status,
        UUID requestedBy,
        Instant computedAt,
        List<AnalysisResultResponse> results) {

    public record AnalysisResultResponse(
            UUID variableId,
            String variableCode,
            BigDecimal minValue,
            BigDecimal maxValue,
            BigDecimal avgValue,
            int sampleCount,
            int exceedanceCount) {
    }

    /**
     * @param variableCodes codigos legibles por variable, resueltos del catalogo
     * @param period        granularidad solicitada por el usuario
     */
    public static AnalysisResponse of(EnvironmentalAnalysis analysis,
                                      AnalysisPeriod period,
                                      Map<UUID, String> variableCodes) {
        List<AnalysisResultResponse> results = analysis.getResults().stream()
                .map(result -> toResponse(result, variableCodes))
                .toList();
        return new AnalysisResponse(
                analysis.getId(),
                analysis.getEducationalEnvironmentId(),
                period,
                analysis.getPeriodStart(),
                analysis.getPeriodEnd(),
                analysis.statusCode(),
                analysis.getRequestedBy(),
                analysis.getComputedAt(),
                results);
    }

    public static Map<UUID, String> variableCodeIndex(List<VariableCatalogPort.VariableRef> variables) {
        return variables.stream().collect(Collectors.toMap(
                VariableCatalogPort.VariableRef::id,
                VariableCatalogPort.VariableRef::code,
                (first, second) -> first));
    }

    private static AnalysisResultResponse toResponse(AnalysisResult result,
                                                     Map<UUID, String> variableCodes) {
        return new AnalysisResultResponse(
                result.getVariableId(),
                variableCodes.get(result.getVariableId()),
                result.getMinValue(),
                result.getMaxValue(),
                result.getAvgValue(),
                result.getSampleCount(),
                result.getExceedanceCount());
    }
}