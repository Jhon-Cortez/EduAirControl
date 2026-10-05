package com.eduaircontrol.backend.modules.analysis.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EnvironmentalAnalysisTest {

    private static final UUID ENVIRONMENT_ID = UUID.randomUUID();
    private static final AnalysisPeriod.Window WINDOW = new AnalysisPeriod.Window(
            Instant.parse("2026-09-01T00:00:00Z"), Instant.parse("2026-10-01T00:00:00Z"));

    @Test
    @DisplayName("A new analysis starts as PENDING with the requested window and no results")
    void startsPending() {
        EnvironmentalAnalysis analysis = EnvironmentalAnalysis.request(
                ENVIRONMENT_ID, WINDOW, AnalysisStatus.pending(), null);

        assertThat(analysis.statusCode()).isEqualTo(AnalysisStatusCode.PENDING);
        assertThat(analysis.getPeriodStart()).isEqualTo(WINDOW.start());
        assertThat(analysis.getPeriodEnd()).isEqualTo(WINDOW.end());
        assertThat(analysis.getEducationalEnvironmentId()).isEqualTo(ENVIRONMENT_ID);
        assertThat(analysis.getComputedAt()).isNull();
        assertThat(analysis.getResults()).isEmpty();
    }

    @Test
    @DisplayName("A new analysis cannot start in a state other than PENDING")
    void rejectsNonPendingInitialStatus() {
        assertThatThrownBy(() -> EnvironmentalAnalysis.request(
                ENVIRONMENT_ID, WINDOW, AnalysisStatus.running(), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("start() moves the analysis to RUNNING")
    void startMovesToRunning() {
        EnvironmentalAnalysis analysis = pending();

        analysis.start(AnalysisStatus.running());

        assertThat(analysis.statusCode()).isEqualTo(AnalysisStatusCode.RUNNING);
    }

    @Test
    @DisplayName("start() rejects a target status that is not RUNNING")
    void startRejectsWrongTarget() {
        EnvironmentalAnalysis analysis = pending();

        assertThatThrownBy(() -> analysis.start(AnalysisStatus.completed()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("complete() attaches the results, links them back and stamps the compute time")
    void completeAttachesResults() {
        EnvironmentalAnalysis analysis = pending();
        analysis.start(AnalysisStatus.running());
        Instant computedAt = Instant.parse("2026-09-30T10:00:00Z");

        analysis.complete(AnalysisStatus.completed(), List.of(result()), computedAt);

        assertThat(analysis.statusCode()).isEqualTo(AnalysisStatusCode.COMPLETED);
        assertThat(analysis.getComputedAt()).isEqualTo(computedAt);
        assertThat(analysis.getResults()).hasSize(1);
        assertThat(analysis.getResults().get(0).getEnvironmentalAnalysis()).isSameAs(analysis);
    }

    @Test
    @DisplayName("complete() requires a computed timestamp")
    void completeRequiresComputedAt() {
        EnvironmentalAnalysis analysis = running();

        assertThatThrownBy(() -> analysis.complete(
                AnalysisStatus.completed(), List.of(), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("complete() is rejected when the analysis is not RUNNING")
    void completeRequiresRunning() {
        EnvironmentalAnalysis analysis = pending();

        assertThatThrownBy(() -> analysis.complete(
                AnalysisStatus.completed(), List.of(), Instant.now()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("fail() moves to FAILED and discards any partial result")
    void failDiscardsResults() {
        EnvironmentalAnalysis analysis = pending();
        analysis.start(AnalysisStatus.running());
        analysis.complete(AnalysisStatus.completed(), List.of(result()), Instant.now());

        EnvironmentalAnalysis retried = running();
        retried.fail(AnalysisStatus.failed());

        assertThat(retried.statusCode()).isEqualTo(AnalysisStatusCode.FAILED);
        assertThat(retried.getResults()).isEmpty();
        assertThat(retried.getComputedAt()).isNull();
    }

    @Test
    @DisplayName("A window whose end is not after its start is rejected")
    void rejectsEmptyWindow() {
        assertThatThrownBy(() -> new AnalysisPeriod.Window(WINDOW.start(), WINDOW.start()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new AnalysisPeriod.Window(WINDOW.end(), WINDOW.start()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new AnalysisPeriod.Window(null, WINDOW.end()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("sameWindow() detects an identical period and ignores a different one")
    void detectsSameWindow() {
        EnvironmentalAnalysis analysis = pending();

        assertThat(analysis.sameWindow(WINDOW)).isTrue();
        assertThat(analysis.sameWindow(new AnalysisPeriod.Window(
                Instant.parse("2026-10-01T00:00:00Z"), Instant.parse("2026-11-01T00:00:00Z"))))
                .isFalse();
        assertThat(analysis.sameWindow(null)).isFalse();
    }

    private EnvironmentalAnalysis pending() {
        return EnvironmentalAnalysis.request(ENVIRONMENT_ID, WINDOW, AnalysisStatus.pending(), null);
    }

    private EnvironmentalAnalysis running() {
        EnvironmentalAnalysis analysis = pending();
        analysis.start(AnalysisStatus.running());
        return analysis;
    }

    private AnalysisResult result() {
        return AnalysisResult.builder()
                .variableId(UUID.randomUUID())
                .minValue(new BigDecimal("21.4000"))
                .maxValue(new BigDecimal("26.9000"))
                .avgValue(new BigDecimal("23.8000"))
                .sampleCount(24)
                .exceedanceCount(2)
                .build();
    }
}