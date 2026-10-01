package com.eduaircontrol.backend.modules.monitoring.application;

import com.eduaircontrol.backend.modules.monitoring.repository.EnvironmentMeasurementRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serie historica agregada del dashboard (promedio por tramo segun el periodo).
 */
@Service
@RequiredArgsConstructor
public class DashboardSeriesService {

    private static final Map<String, Bucket> BUCKETS = Map.of(
            "day", new Bucket("hour", 24, ChronoUnit.HOURS),
            "week", new Bucket("day", 7, ChronoUnit.DAYS),
            "month", new Bucket("day", 30, ChronoUnit.DAYS),
            "year", new Bucket("month", 365, ChronoUnit.DAYS));

    private final EnvironmentMeasurementRepository measurementRepository;

    @Transactional(readOnly = true)
    public List<SeriesPoint> series(String period, String variable, UUID environmentId) {
        String normalizedPeriod = period == null ? "day" : period.trim().toLowerCase(Locale.ROOT);
        Bucket bucket = BUCKETS.getOrDefault(normalizedPeriod, BUCKETS.get("day"));
        String normalizedVariable = variable == null || variable.isBlank()
                ? "temperature"
                : variable.trim().toLowerCase(Locale.ROOT);

        Instant from = Instant.now().minus(bucket.amount(), bucket.unit());
        return measurementRepository.findSeries(bucket.trunc(), normalizedVariable, from, environmentId).stream()
                .map(row -> new SeriesPoint(
                        row.getBucket() != null ? row.getBucket().toInstant() : null,
                        row.getAvg_value(),
                        row.getSamples() != null ? row.getSamples() : 0L))
                .toList();
    }

    private record Bucket(String trunc, int amount, ChronoUnit unit) {
    }

    public record SeriesPoint(Instant bucket, BigDecimal value, long samples) {
    }
}
