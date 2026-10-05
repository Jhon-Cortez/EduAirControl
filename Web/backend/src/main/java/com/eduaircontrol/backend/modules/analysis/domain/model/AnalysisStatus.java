package com.eduaircontrol.backend.modules.analysis.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Catalogo de estados del analisis. Las filas estan sembradas por Liquibase;
 * la entidad solo las lee.
 */
@Entity
@Table(name = "analysis_statuses", schema = "monitoring")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisStatus {

    public static final String NAME_PENDING = "Pendiente";
    public static final String NAME_RUNNING = "En ejecución";
    public static final String NAME_COMPLETED = "Completado";
    public static final String NAME_FAILED = "Fallido";

    @Id
    @Column(name = "analysis_status_id")
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 40)
    private String name;

    public static AnalysisStatus of(AnalysisStatusCode code, String name) {
        return new AnalysisStatus(null, code.name(), name);
    }

    public static AnalysisStatus pending() {
        return of(AnalysisStatusCode.PENDING, NAME_PENDING);
    }

    public static AnalysisStatus running() {
        return of(AnalysisStatusCode.RUNNING, NAME_RUNNING);
    }

    public static AnalysisStatus completed() {
        return of(AnalysisStatusCode.COMPLETED, NAME_COMPLETED);
    }

    public static AnalysisStatus failed() {
        return of(AnalysisStatusCode.FAILED, NAME_FAILED);
    }

    public AnalysisStatusCode statusCode() {
        return AnalysisStatusCode.fromCode(code);
    }

    public boolean is(AnalysisStatusCode expected) {
        return statusCode() == expected;
    }
}