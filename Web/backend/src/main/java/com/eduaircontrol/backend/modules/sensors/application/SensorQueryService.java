package com.eduaircontrol.backend.modules.sensors.application;

import com.eduaircontrol.backend.modules.sensors.dto.SensorRowResponse;
import com.eduaircontrol.backend.shared.contract.MeasurementSnapshotPort;
import com.eduaircontrol.backend.shared.contract.ThresholdPort;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SensorQueryService {

    private static final Duration OFFLINE_AFTER = Duration.ofMinutes(30);

    private final SensorRowDao sensorRowDao;
    private final ThresholdPort thresholdPort;
    private final MeasurementSnapshotPort measurementSnapshotPort;

    @Transactional(readOnly = true)
    public List<SensorRowResponse> list(UUID environmentId) {
        List<SensorRowDao.ActiveSensorRow> rows = sensorRowDao.findRows(environmentId);
        if (rows.isEmpty()) {
            return List.of();
        }

        Map<UUID, Map<UUID, ThresholdPort.Range>> rangesByEnvironment = new HashMap<>();
        Map<UUID, MeasurementSnapshotPort.Snapshot> snapshots = new HashMap<>();
        rows.stream().map(SensorRowDao.ActiveSensorRow::environmentId).filter(Objects::nonNull).distinct()
                .forEach(id -> {
                    Map<UUID, ThresholdPort.Range> byVariable = new HashMap<>();
                    thresholdPort.warningRanges(id).forEach(range -> byVariable.put(range.variableId(), range));
                    rangesByEnvironment.put(id, byVariable);
                });

        rows.stream().map(SensorRowDao.ActiveSensorRow::environmentId).filter(Objects::nonNull).distinct()
                .forEach(id -> snapshots.put(id, measurementSnapshotPort.snapshot(id)));

        Instant now = Instant.now();
        return rows.stream()
                .map(row -> {
                    ThresholdPort.Range range =
                            rangesByEnvironment.getOrDefault(row.environmentId(), Map.of())
                                    .get(row.variableId());
                    BigDecimal value = snapshots.getOrDefault(row.environmentId(), emptySnapshot())
                            .values().get(row.variableCode());
                    // Sensor retirado: se muestra como offline hasta que se reactive.
                    String status = row.installed()
                            ? resolveStatus(row, value, range, now)
                            : "offline";
                    boolean active = row.installed() && !"offline".equals(status);
                    return new SensorRowResponse(
                            row.serialNumber(),
                            row.sensorId(),
                            row.environmentId(),
                            row.variableCode(),
                            active,
                            status,
                            relative(row.lastSeenAt(), now),
                            range != null ? range.min() : null,
                            range != null ? range.max() : null,
                            row.installed());
                })
                .sorted(Comparator.comparing(SensorRowResponse::id))
                .toList();
    }

    @Transactional(readOnly = true)
    public SensorRowResponse get(String serial) {
        return list(null).stream()
                .filter(row -> serial.equals(row.id()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor no encontrado"));
    }

    private String resolveStatus(SensorRowDao.ActiveSensorRow row, BigDecimal value,
                                 ThresholdPort.Range range, Instant now) {
        if (row.lastSeenAt() == null || Duration.between(row.lastSeenAt(), now).compareTo(OFFLINE_AFTER) > 0) {
            return "offline";
        }
        if (range != null && value != null) {
            if (range.max() != null && value.compareTo(range.max()) > 0) {
                return "warning";
            }
            if (range.min() != null && value.compareTo(range.min()) < 0) {
                return "warning";
            }
        }
        return "active";
    }

    private MeasurementSnapshotPort.Snapshot emptySnapshot() {
        return new MeasurementSnapshotPort.Snapshot(Map.of(), null, "dashboard.statusNormal");
    }

    private String relative(Instant instant, Instant now) {
        if (instant == null) {
            return "Sin datos";
        }
        Duration elapsed = Duration.between(instant, now);
        if (elapsed.compareTo(Duration.ofMinutes(1)) < 0) {
            return "Ahora";
        }
        long minutes = elapsed.toMinutes();
        if (minutes < 60) {
            return "Hace " + minutes + " min";
        }
        long hours = elapsed.toHours();
        if (hours < 24) {
            return "Hace " + hours + " h";
        }
        return "Hace " + ChronoUnit.DAYS.between(instant, now) + " d";
    }
}
