package com.eduaircontrol.backend.shared.contract;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Contrato de lecturas actuales y estado derivado (modulo monitoring).
 * Devuelve por ambiente los valores de las variables (temperature/humidity/co2/noise)
 * y el estado calculado frente a los umbrales: NORMAL / WARNING / ALERT.
 */
public interface MeasurementSnapshotPort {

    String STATUS_NORMAL = "dashboard.statusNormal";
    String STATUS_WARNING = "dashboard.statusWarning";
    String STATUS_ALERT = "dashboard.statusAlert";

    Map<UUID, Snapshot> latestByEnvironments(Collection<UUID> environmentIds);

    Snapshot snapshot(UUID environmentId);

    Set<UUID> environmentsOnAlert();

    record Snapshot(
            Map<String, BigDecimal> values,
            Instant measuredAt,
            String statusKey) {
    }

    record StatusCounts(int normal, int warning, int alert, int total) {
    }

}
