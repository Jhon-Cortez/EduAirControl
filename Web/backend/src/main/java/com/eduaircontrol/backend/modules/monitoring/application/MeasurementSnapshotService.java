package com.eduaircontrol.backend.modules.monitoring.application;

import com.eduaircontrol.backend.modules.monitoring.entity.Severity;
import com.eduaircontrol.backend.modules.monitoring.entity.VariableThreshold;
import com.eduaircontrol.backend.modules.monitoring.repository.EnvironmentMeasurementRepository;
import com.eduaircontrol.backend.modules.monitoring.repository.SeverityRepository;
import com.eduaircontrol.backend.modules.monitoring.repository.VariableThresholdRepository;
import com.eduaircontrol.backend.shared.contract.EnvironmentLookupPort;
import com.eduaircontrol.backend.shared.contract.MeasurementSnapshotPort;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MeasurementSnapshotService implements MeasurementSnapshotPort {

    public static final String STATUS_NORMAL = MeasurementSnapshotPort.STATUS_NORMAL;
    public static final String STATUS_WARNING = MeasurementSnapshotPort.STATUS_WARNING;
    public static final String STATUS_ALERT = MeasurementSnapshotPort.STATUS_ALERT;

    private final EnvironmentMeasurementRepository measurementRepository;
    private final VariableThresholdRepository thresholdRepository;
    private final SeverityRepository severityRepository;
    private final EnvironmentLookupPort environmentLookupPort;

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, Snapshot> latestByEnvironments(Collection<UUID> environmentIds) {
        return latestByEnvironmentIds(environmentIds == null ? List.of() : List.copyOf(environmentIds));
    }

    @Override
    @Transactional(readOnly = true)
    public Snapshot snapshot(UUID environmentId) {
        return latestByEnvironmentIds(List.of(environmentId))
                .getOrDefault(environmentId, new Snapshot(Map.of(), null, STATUS_NORMAL));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UUID> environmentsOnAlert() {
        List<UUID> all = environmentLookupPort.findAll().stream()
                .map(EnvironmentLookupPort.EnvironmentInfo::id)
                .toList();
        return latestByEnvironmentIds(all).entrySet().stream()
                .filter(entry -> STATUS_ALERT.equals(entry.getValue().statusKey()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private Map<UUID, Snapshot> latestByEnvironmentIds(List<UUID> environmentIds) {
        if (environmentIds == null || environmentIds.isEmpty()) {
            return Map.of();
        }
        List<EnvironmentMeasurementRepository.LatestRow> rows =
                measurementRepository.findLatestByEnvironment(environmentIds);
        if (rows.isEmpty()) {
            return Map.of();
        }

        Map<UUID, EnvironmentLookupPort.EnvironmentInfo> environments = environmentLookupPort.findAll().stream()
                .filter(environment -> environmentIds.contains(environment.id()))
                .collect(Collectors.toMap(EnvironmentLookupPort.EnvironmentInfo::id, Function.identity()));

        List<UUID> typeIds = environments.values().stream()
                .map(EnvironmentLookupPort.EnvironmentInfo::environmentTypeId)
                .distinct()
                .toList();

        UUID warningId = severityId("WARNING");
        UUID criticalId = severityId("CRITICAL");

        Map<UUID, Map<UUID, VariableThreshold>> thresholds = new HashMap<>();
        List<VariableThreshold> effective = new ArrayList<>();
        if (!typeIds.isEmpty()) {
            effective.addAll(thresholdRepository.findEffectiveBySeverity(environmentIds, typeIds, warningId));
            effective.addAll(thresholdRepository.findEffectiveBySeverity(environmentIds, typeIds, criticalId));
        }
        effective.forEach(threshold -> {
            UUID environmentId = threshold.getEducationalEnvironmentId() != null
                    ? threshold.getEducationalEnvironmentId()
                    : environments.entrySet().stream()
                            .filter(entry -> entry.getValue().environmentTypeId()
                                    .equals(threshold.getEnvironmentTypeId()))
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElse(null);
            if (environmentId == null) {
                return;
            }
            Map<UUID, VariableThreshold> byVariable = thresholds
                    .computeIfAbsent(environmentId, key -> new HashMap<>());
            // Precedencia: el umbral propio del ambiente pisa al del tipo.
            if (byVariable.containsKey(threshold.getVariableId())
                    && byVariable.get(threshold.getVariableId()).getEducationalEnvironmentId() != null) {
                return;
            }
            byVariable.put(threshold.getVariableId(), threshold);
        });

        Map<UUID, Map<String, BigDecimal>> valuesByEnvironment = new HashMap<>();
        Map<UUID, java.time.Instant> measuredAtByEnvironment = new HashMap<>();
        Map<UUID, String> statusByEnvironment = new HashMap<>();

        rows.forEach(row -> {
            UUID environmentId = row.getEnvironment_id();
            valuesByEnvironment.computeIfAbsent(environmentId, key -> new HashMap<>())
                    .put(row.getVariable_code(), row.getMeasured_value());
            if (row.getMeasured_at() != null) {
                measuredAtByEnvironment.merge(environmentId, row.getMeasured_at(),
                        (first, second) -> first.isAfter(second) ? first : second);
            }
            String status = resolveStatus(thresholds.getOrDefault(environmentId, Map.of()),
                    row.getVariable_id(), row.getMeasured_value(), warningId, criticalId);
            String current = statusByEnvironment.get(environmentId);
            if (current == null || severity(status) > severity(current)) {
                statusByEnvironment.put(environmentId, status);
            }
        });

        Map<UUID, Snapshot> result = new HashMap<>();
        environments.keySet().forEach(environmentId -> result.put(environmentId, new Snapshot(
                valuesByEnvironment.getOrDefault(environmentId, Map.of()),
                measuredAtByEnvironment.get(environmentId),
                statusByEnvironment.getOrDefault(environmentId, STATUS_NORMAL))));
        rows.forEach(row -> result.putIfAbsent(row.getEnvironment_id(),
                new Snapshot(Map.of(), row.getMeasured_at(), STATUS_NORMAL)));
        return result;
    }

    private String resolveStatus(Map<UUID, VariableThreshold> thresholds, UUID variableId, BigDecimal value,
                                 UUID warningId, UUID criticalId) {
        if (value == null) {
            return STATUS_NORMAL;
        }
        VariableThreshold threshold = thresholds.get(variableId);
        if (threshold == null) {
            return STATUS_NORMAL;
        }
        if (criticalId.equals(threshold.getSeverityId()) && exceeds(threshold, value)) {
            return STATUS_ALERT;
        }
        if (warningId.equals(threshold.getSeverityId()) && exceeds(threshold, value)) {
            return STATUS_WARNING;
        }
        return STATUS_NORMAL;
    }

    private boolean exceeds(VariableThreshold threshold, BigDecimal value) {
        boolean aboveMax = threshold.getMaxValue() != null && value.compareTo(threshold.getMaxValue()) > 0;
        boolean belowMin = threshold.getMinValue() != null && value.compareTo(threshold.getMinValue()) < 0;
        return aboveMax || belowMin;
    }

    private int severity(String status) {
        if (STATUS_ALERT.equals(status)) {
            return 2;
        }
        return STATUS_WARNING.equals(status) ? 1 : 0;
    }

    private UUID severityId(String code) {
        return severityRepository.findByCode(code)
                .map(Severity::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Severidad no sembrada: " + code));
    }
}
