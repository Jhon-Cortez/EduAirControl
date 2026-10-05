package com.eduaircontrol.backend.modules.analysis.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.eduaircontrol.backend.modules.analysis.domain.model.AnalysisPeriod;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class PeriodResolverTest {

    private static final ZoneOffset UTC = ZoneOffset.UTC;

    private final PeriodResolver resolver = new PeriodResolver();

    @Test
    @DisplayName("DAY resolves a single UTC calendar day with an exclusive end")
    void resolvesDay() {
        Instant reference = LocalDate.of(2026, 9, 4).atStartOfDay(UTC).toInstant();

        AnalysisPeriod.Window window = resolver.resolve(AnalysisPeriod.DAY, reference);

        assertThat(window.start()).isEqualTo(Instant.parse("2026-09-04T00:00:00Z"));
        assertThat(window.end()).isEqualTo(Instant.parse("2026-09-05T00:00:00Z"));
    }

    @Test
    @DisplayName("WEEK resolves Monday to Monday in UTC")
    void resolvesWeek() {
        // 2026-09-04 is a Friday.
        Instant reference = LocalDate.of(2026, 9, 4).atStartOfDay(UTC).toInstant();

        AnalysisPeriod.Window window = resolver.resolve(AnalysisPeriod.WEEK, reference);

        assertThat(window.start()).isEqualTo(Instant.parse("2026-08-31T00:00:00Z"));
        assertThat(window.end()).isEqualTo(Instant.parse("2026-09-07T00:00:00Z"));
    }

    @Test
    @DisplayName("MONTH resolves the first day to the first day of the next month")
    void resolvesMonth() {
        Instant reference = Instant.parse("2026-09-04T13:45:00Z");

        AnalysisPeriod.Window window = resolver.resolve(AnalysisPeriod.MONTH, reference);

        assertThat(window.start()).isEqualTo(Instant.parse("2026-09-01T00:00:00Z"));
        assertThat(window.end()).isEqualTo(Instant.parse("2026-10-01T00:00:00Z"));
    }

    @Test
    @DisplayName("YEAR resolves January first to the next January first")
    void resolvesYear() {
        Instant reference = Instant.parse("2026-09-04T13:45:00Z");

        AnalysisPeriod.Window window = resolver.resolve(AnalysisPeriod.YEAR, reference);

        assertThat(window.start()).isEqualTo(Instant.parse("2026-01-01T00:00:00Z"));
        assertThat(window.end()).isEqualTo(Instant.parse("2027-01-01T00:00:00Z"));
    }

    @Test
    @DisplayName("MONTH handles February in a leap year")
    void resolvesLeapFebruary() {
        Instant reference = Instant.parse("2028-02-10T00:00:00Z");

        AnalysisPeriod.Window window = resolver.resolve(AnalysisPeriod.MONTH, reference);

        assertThat(window.end()).isEqualTo(Instant.parse("2028-03-01T00:00:00Z"));
    }

    @Test
    @DisplayName("MONTH handles December rolling into the next year")
    void resolvesDecemberRollover() {
        Instant reference = Instant.parse("2026-12-20T00:00:00Z");

        AnalysisPeriod.Window window = resolver.resolve(AnalysisPeriod.MONTH, reference);

        assertThat(window.start()).isEqualTo(Instant.parse("2026-12-01T00:00:00Z"));
        assertThat(window.end()).isEqualTo(Instant.parse("2027-01-01T00:00:00Z"));
    }

    @Test
    @DisplayName("The time of day of the reference instant is ignored")
    void ignoresTimeOfDay() {
        Instant morning = Instant.parse("2026-09-04T00:00:01Z");
        Instant evening = Instant.parse("2026-09-04T23:59:59Z");

        assertThat(resolver.resolve(AnalysisPeriod.DAY, morning))
                .isEqualTo(resolver.resolve(AnalysisPeriod.DAY, evening));
    }

    @ParameterizedTest
    @EnumSource(AnalysisPeriod.class)
    @DisplayName("Every period produces a positive window that satisfies period_start < period_end")
    void producesValidWindow(AnalysisPeriod period) {
        Instant reference = Instant.parse("2026-09-04T13:45:00Z");

        AnalysisPeriod.Window window = resolver.resolve(period, reference);

        assertThat(window.start()).isBefore(window.end());
        assertThat(window.start()).isEqualTo(resolveStart(period, reference));
    }

    @Test
    @DisplayName("A null period is rejected")
    void rejectsNullPeriod() {
        Instant reference = Instant.parse("2026-09-04T00:00:00Z");

        assertThatThrownBy(() -> resolver.resolve(null, reference))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Instant resolveStart(AnalysisPeriod period, Instant reference) {
        LocalDate date = reference.atZone(UTC).toLocalDate();
        return switch (period) {
            case DAY -> date.atStartOfDay(UTC).toInstant();
            case WEEK -> date.with(java.time.DayOfWeek.MONDAY).atStartOfDay(UTC).toInstant();
            case MONTH -> date.withDayOfMonth(1).atStartOfDay(UTC).toInstant();
            case YEAR -> date.withDayOfYear(1).atStartOfDay(UTC).toInstant();
        };
    }
}