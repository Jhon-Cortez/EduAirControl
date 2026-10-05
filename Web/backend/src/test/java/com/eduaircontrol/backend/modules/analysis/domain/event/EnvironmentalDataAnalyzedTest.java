package com.eduaircontrol.backend.modules.analysis.domain.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EnvironmentalDataAnalyzedTest {

    @Test
    @DisplayName("The event carries the period, the window and one summary per variable")
    void carriesAnalysisOutcome() {
        Instant start = Instant.parse("2026-09-01T00:00:00Z");
        Instant end = Instant.parse("2026-10-01T00:00:00Z");
        Instant occurredAt = Instant.parse("2026-09-30T10:00:00Z");

        EnvironmentalDataAnalyzed event = new EnvironmentalDataAnalyzed(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "MONTH",
                start, end, occurredAt,
                List.of(new EnvironmentalDataAnalyzed.VariableSummary("TEMP",
                        new BigDecimal("21.4000"), new BigDecimal("26.9000"),
                        new BigDecimal("23.8000"), 24, 2)));

        assertThat(event.period()).isEqualTo("MONTH");
        assertThat(event.periodStart()).isEqualTo(start);
        assertThat(event.periodEnd()).isEqualTo(end);
        assertThat(event.occurredAt()).isEqualTo(occurredAt);
        assertThat(event.results()).hasSize(1);
        assertThat(event.results().get(0).variableCode()).isEqualTo("TEMP");
        assertThat(event.results().get(0).exceedanceCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("A null result list becomes an immutable empty list")
    void normalizesNullResults() {
        EnvironmentalDataAnalyzed event = new EnvironmentalDataAnalyzed(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "DAY",
                Instant.parse("2026-09-04T00:00:00Z"), Instant.parse("2026-09-05T00:00:00Z"),
                Instant.parse("2026-09-04T10:00:00Z"), null);

        assertThat(event.results()).isEmpty();
        assertThatThrownBy(() -> event.results().add(null))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("The result list is copied defensively")
    void copiesResults() {
        List<EnvironmentalDataAnalyzed.VariableSummary> source = new ArrayList<>();
        source.add(new EnvironmentalDataAnalyzed.VariableSummary("HUM",
                BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ONE, 5, 0));

        EnvironmentalDataAnalyzed event = new EnvironmentalDataAnalyzed(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "DAY",
                Instant.parse("2026-09-04T00:00:00Z"), Instant.parse("2026-09-05T00:00:00Z"),
                Instant.parse("2026-09-04T10:00:00Z"), source);
        source.clear();

        assertThat(event.results()).hasSize(1);
    }
}