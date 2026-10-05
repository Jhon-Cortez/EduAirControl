package com.eduaircontrol.backend.modules.analysis.domain.service;

import com.eduaircontrol.backend.modules.analysis.domain.model.AnalysisPeriod;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Resuelve un periodo a una ventana concreta [start, end) en UTC.
 *
 * <p>Es logica pura y determinista: recibe la fecha de referencia y no depende del
 * reloj ni de la zona horaria del servidor, de modo que el mismo periodo produce
 * siempre la misma ventana.
 */
public class PeriodResolver {

    private static final ZoneOffset UTC = ZoneOffset.UTC;

    private final Clock clock;

    public PeriodResolver() {
        this(Clock.systemUTC());
    }

    public PeriodResolver(Clock clock) {
        this.clock = clock;
    }

    /** Resuelve el periodo que contiene el instante actual. */
    public AnalysisPeriod.Window resolve(AnalysisPeriod period) {
        return resolve(period, clock.instant());
    }

    /**
     * Resuelve el periodo que contiene el instante de referencia. La hora del dia se
     * ignora: el periodo se calcula sobre la fecha en UTC.
     */
    public AnalysisPeriod.Window resolve(AnalysisPeriod period, Instant reference) {
        if (period == null) {
            throw new IllegalArgumentException("period is required");
        }
        if (reference == null) {
            throw new IllegalArgumentException("reference instant is required");
        }

        LocalDate date = reference.atZone(UTC).toLocalDate();
        LocalDate start = switch (period) {
            case DAY -> date;
            case WEEK -> date.with(DayOfWeek.MONDAY);
            case MONTH -> date.withDayOfMonth(1);
            case YEAR -> date.withDayOfYear(1);
        };
        LocalDate end = switch (period) {
            case DAY -> start.plusDays(1);
            case WEEK -> start.plusWeeks(1);
            case MONTH -> start.plusMonths(1);
            case YEAR -> start.plusYears(1);
        };
        return new AnalysisPeriod.Window(start.atStartOfDay(UTC).toInstant(),
                end.atStartOfDay(UTC).toInstant());
    }
}