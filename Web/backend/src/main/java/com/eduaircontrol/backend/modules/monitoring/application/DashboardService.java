package com.eduaircontrol.backend.modules.monitoring.application;

import com.eduaircontrol.backend.modules.monitoring.repository.EnvironmentMeasurementRepository;
import com.eduaircontrol.backend.shared.contract.EnvironmentLookupPort;
import com.eduaircontrol.backend.shared.contract.MeasurementSnapshotPort;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resumen del dashboard: conteos por estado, promedio actual de cada variable
 * y fecha de la ultima lectura, derivados de datos reales.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MeasurementSnapshotPort measurementSnapshotPort;
    private final EnvironmentLookupPort environmentLookupPort;
    private final EnvironmentMeasurementRepository measurementRepository;

    @Transactional(readOnly = true)
    public DashboardSummary summary() {
        List<UUID> environmentIds = environmentLookupPort.findAll().stream()
                .map(EnvironmentLookupPort.EnvironmentInfo::id)
                .toList();
        Map<UUID, MeasurementSnapshotPort.Snapshot> snapshots =
                measurementSnapshotPort.latestByEnvironments(environmentIds);

        int normal = 0;
        int warning = 0;
        int alert = 0;
        Map<String, List<BigDecimal>> values = new HashMap<>();
        Instant lastUpdated = null;

        for (MeasurementSnapshotPort.Snapshot snapshot : snapshots.values()) {
            if (MeasurementSnapshotPort.STATUS_ALERT.equals(snapshot.statusKey())) {
                alert++;
            } else if (MeasurementSnapshotPort.STATUS_WARNING.equals(snapshot.statusKey())) {
                warning++;
            } else {
                normal++;
            }
            snapshot.values().forEach((code, value) ->
                    values.computeIfAbsent(code, key -> new ArrayList<>()).add(value));
            if (snapshot.measuredAt() != null
                    && (lastUpdated == null || snapshot.measuredAt().isAfter(lastUpdated))) {
                lastUpdated = snapshot.measuredAt();
            }
        }

        Map<String, BigDecimal> averages = new HashMap<>();
        values.forEach((code, list) -> averages.put(code, average(list)));

        if (lastUpdated == null) {
            lastUpdated = measurementRepository.findLatestMeasuredAt(null);
        }

        return new DashboardSummary(
                new MeasurementSnapshotPort.StatusCounts(normal, warning, alert, normal + warning + alert),
                averages,
                lastUpdated);
    }

    private BigDecimal average(List<BigDecimal> values) {
        if (values.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(values.size()), 1, RoundingMode.HALF_UP);
    }

    public record DashboardSummary(MeasurementSnapshotPort.StatusCounts counts,
                                   Map<String, BigDecimal> averages,
                                   Instant lastUpdated) {
    }
}
