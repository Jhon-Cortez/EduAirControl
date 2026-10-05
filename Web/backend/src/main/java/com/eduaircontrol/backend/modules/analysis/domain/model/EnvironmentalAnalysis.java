package com.eduaircontrol.backend.modules.analysis.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Analisis historico de un ambiente en una ventana concreta. Es el agregado raiz:
 * materializa el periodo analizado y sus resultados por variable.
 *
 * <p>El ciclo de vida es PENDING -> RUNNING -> COMPLETED | FAILED y se persiste para
 * que el estado sea auditable; el calculo se ejecuta de forma sincrona.
 */
@Entity
@Table(name = "environmental_analysis", schema = "monitoring")
@Getter
@Setter
@NoArgsConstructor
public class EnvironmentalAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "environmental_analysis_id", updatable = false)
    private UUID id;

    @Column(name = "educational_environment_id", nullable = false, updatable = false)
    private UUID educationalEnvironmentId;

    @Column(name = "period_start", nullable = false, updatable = false)
    private Instant periodStart;

    @Column(name = "period_end", nullable = false, updatable = false)
    private Instant periodEnd;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "analysis_status_id", nullable = false)
    private AnalysisStatus status;

    @Column(name = "requested_by", updatable = false)
    private UUID requestedBy;

    @Column(name = "computed_at")
    private Instant computedAt;

    @OneToMany(mappedBy = "environmentalAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnalysisResult> results = new ArrayList<>();

    public static EnvironmentalAnalysis request(UUID educationalEnvironmentId,
                                                AnalysisPeriod.Window window,
                                                AnalysisStatus pending,
                                                UUID requestedBy) {
        if (window == null) {
            throw new IllegalArgumentException("window is required");
        }
        if (pending == null || !pending.is(AnalysisStatusCode.PENDING)) {
            throw new IllegalArgumentException("a new analysis must start as PENDING");
        }
        EnvironmentalAnalysis analysis = new EnvironmentalAnalysis();
        analysis.educationalEnvironmentId = educationalEnvironmentId;
        analysis.periodStart = window.start();
        analysis.periodEnd = window.end();
        analysis.status = pending;
        analysis.requestedBy = requestedBy;
        return analysis;
    }

    public void start(AnalysisStatus running) {
        requireStatus(AnalysisStatusCode.PENDING);
        if (running == null || !running.is(AnalysisStatusCode.RUNNING)) {
            throw new IllegalArgumentException("target status must be RUNNING");
        }
        this.status = running;
    }

    public void complete(AnalysisStatus completed, List<AnalysisResult> aggregated, Instant computedAt) {
        requireStatus(AnalysisStatusCode.RUNNING);
        if (completed == null || !completed.is(AnalysisStatusCode.COMPLETED)) {
            throw new IllegalArgumentException("target status must be COMPLETED");
        }
        if (computedAt == null) {
            throw new IllegalArgumentException("computedAt is required");
        }
        this.results.clear();
        if (aggregated != null) {
            aggregated.forEach(result -> {
                result.setEnvironmentalAnalysis(this);
                this.results.add(result);
            });
        }
        this.status = completed;
        this.computedAt = computedAt;
    }

    public void fail(AnalysisStatus failed) {
        requireStatus(AnalysisStatusCode.RUNNING);
        if (failed == null || !failed.is(AnalysisStatusCode.FAILED)) {
            throw new IllegalArgumentException("target status must be FAILED");
        }
        this.results.clear();
        this.status = failed;
    }

    /** Informa si la ventana de otro analisis coincide con la de este. */
    public boolean sameWindow(AnalysisPeriod.Window other) {
        return other != null
                && periodStart.equals(other.start())
                && periodEnd.equals(other.end());
    }

    public AnalysisStatusCode statusCode() {
        return status == null ? null : status.statusCode();
    }

    private void requireStatus(AnalysisStatusCode expected) {
        if (statusCode() != expected) {
            throw new IllegalStateException("expected status " + expected + " but was " + statusCode());
        }
    }
}