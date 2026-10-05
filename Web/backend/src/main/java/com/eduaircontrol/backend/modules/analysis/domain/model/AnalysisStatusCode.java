package com.eduaircontrol.backend.modules.analysis.domain.model;

/**
 * Ciclo de vida de un analisis. Los codigos coinciden con los registros del
 * catalogo {@code monitoring.analysis_statuses}.
 */
public enum AnalysisStatusCode {

    PENDING,
    RUNNING,
    COMPLETED,
    FAILED;

    public static AnalysisStatusCode fromCode(String code) {
        for (AnalysisStatusCode candidate : values()) {
            if (candidate.name().equals(code)) {
                return candidate;
            }
        }
        throw new IllegalArgumentException("unknown analysis status code: " + code);
    }
}