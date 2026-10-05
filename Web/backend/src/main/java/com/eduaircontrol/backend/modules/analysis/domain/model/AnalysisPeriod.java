package com.eduaircontrol.backend.modules.analysis.domain.model;

import java.time.Instant;

/**
 * Granularidad del periodo analizado. Cada periodo se resuelve a una ventana
 * concreta [start, end) en UTC por {@code PeriodResolver}.
 */
public enum AnalysisPeriod {

    DAY,
    WEEK,
    MONTH,
    YEAR;

    /**
     * Ventana temporal materializada del periodo. El inicio es inclusivo y el
     * final es exclusivo, de modo que dos periodos consecutivos nunca se solapan.
     */
    public record Window(Instant start, Instant end) {

        public Window {
            if (start == null || end == null) {
                throw new IllegalArgumentException("window bounds are required");
            }
            if (!start.isBefore(end)) {
                throw new IllegalArgumentException("period_start must be before period_end");
            }
        }
    }
}