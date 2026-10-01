package com.eduaircontrol.backend.modules.monitoring.application;

import com.eduaircontrol.backend.modules.monitoring.entity.EnvironmentAlert;
import com.eduaircontrol.backend.modules.monitoring.entity.Severity;
import com.eduaircontrol.backend.modules.monitoring.entity.VariableThreshold;
import com.eduaircontrol.backend.modules.monitoring.repository.EnvironmentAlertRepository;
import com.eduaircontrol.backend.modules.monitoring.repository.EnvironmentMeasurementRepository;
import com.eduaircontrol.backend.modules.monitoring.repository.SeverityRepository;
import com.eduaircontrol.backend.modules.monitoring.repository.VariableThresholdRepository;
import com.eduaircontrol.backend.shared.contract.EnvironmentLookupPort;
import com.eduaircontrol.backend.shared.contract.MeasurementSnapshotPort;
import com.eduaircontrol.backend.shared.contract.NotificationStatePort;
import com.eduaircontrol.backend.shared.contract.UserIdentityPort;
import com.eduaircontrol.backend.shared.contract.VariableCatalogPort;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Notificaciones: alertas ambientales con su estado de lectura por usuario.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EnvironmentAlertRepository alertRepository;
    private final EnvironmentMeasurementRepository measurementRepository;
    private final VariableThresholdRepository thresholdRepository;
    private final SeverityRepository severityRepository;
    private final EnvironmentLookupPort environmentLookupPort;
    private final VariableCatalogPort variableCatalogPort;
    private final UserIdentityPort userIdentityPort;
    private final NotificationStatePort notificationStatePort;

    @Transactional(readOnly = true)
    public List<NotificationResponse> list(String email) {
        List<EnvironmentAlert> alerts = alertRepository.findTop50ByDeletedAtIsNullOrderByRaisedAtDesc();
        if (alerts.isEmpty()) {
            return List.of();
        }
        UUID userId = userIdentityPort.idByEmail(email).orElse(null);
        Set<UUID> read = userId == null ? Set.of() : notificationStatePort.readAlertIds(userId);

        Map<UUID, EnvironmentLookupPort.EnvironmentInfo> environments = environmentLookupPort.findAll().stream()
                .collect(Collectors.toMap(EnvironmentLookupPort.EnvironmentInfo::id, Function.identity()));
        Map<UUID, VariableCatalogPort.VariableRef> variables = variableCatalogPort.findAll().stream()
                .collect(Collectors.toMap(VariableCatalogPort.VariableRef::id, Function.identity()));

        return alerts.stream()
                .map(alert -> {
                    EnvironmentLookupPort.EnvironmentInfo environment = environments.get(alert.getEducationalEnvironmentId());
                    VariableCatalogPort.VariableRef variable = variables.get(alert.getVariableId());
                    return new NotificationResponse(
                            alert.getId(),
                            alert.getEducationalEnvironmentId(),
                            environment != null ? environment.name() : null,
                            variable != null ? variable.code() : null,
                            valueOf(alert.getTriggeringMeasurementId()),
                            severityOf(alert),
                            alert.getResolvedAt() != null ? "RESOLVED" : "OPEN",
                            alert.getRaisedAt(),
                            read.contains(alert.getId()) ? instantOf(alert.getId(), userId) : null);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public long unreadCount(String email) {
        UUID userId = userIdentityPort.idByEmail(email).orElse(null);
        if (userId == null) {
            return alertRepository.countByResolvedAtIsNullAndDeletedAtIsNull();
        }
        Set<UUID> read = notificationStatePort.readAlertIds(userId);
        return alertRepository.findTop50ByDeletedAtIsNullOrderByRaisedAtDesc().stream()
                .filter(alert -> alert.getResolvedAt() == null)
                .filter(alert -> !read.contains(alert.getId()))
                .count();
    }

    @Transactional
    public void markRead(String email, UUID alertId) {
        UUID userId = requireUserId(email);
        notificationStatePort.markRead(userId, alertId);
    }

    @Transactional
    public void markAllRead(String email) {
        UUID userId = requireUserId(email);
        Set<UUID> openIds = alertRepository.findTop50ByDeletedAtIsNullOrderByRaisedAtDesc().stream()
                .filter(alert -> alert.getResolvedAt() == null)
                .map(EnvironmentAlert::getId)
                .collect(Collectors.toSet());
        notificationStatePort.markAllRead(userId, openIds);
    }

    private UUID requireUserId(String email) {
        return userIdentityPort.idByEmail(email)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.UNAUTHORIZED, "Sesión inválida"));
    }

    private Instant instantOf(UUID alertId, UUID userId) {
        if (userId == null) {
            return null;
        }
        return notificationStatePort.readAt(userId, alertId);
    }

    private BigDecimal valueOf(UUID measurementId) {
        if (measurementId == null) {
            return null;
        }
        return measurementRepository.findById(measurementId)
                .map(measurement -> measurement.getMeasuredValue())
                .orElse(null);
    }

    private String severityOf(EnvironmentAlert alert) {
        Optional<VariableThreshold> threshold = thresholdRepository.findById(alert.getVariableThresholdId());
        if (threshold.isEmpty()) {
            return "INFO";
        }
        return severityRepository.findById(threshold.get().getSeverityId())
                .map(Severity::getCode)
                .orElse("INFO");
    }

    public record NotificationResponse(
            UUID id,
            UUID environmentId,
            String environmentName,
            String variable,
            BigDecimal value,
            String severity,
            String status,
            Instant raisedAt,
            Instant readAt) {

        public boolean isRead() {
            return readAt != null;
        }
    }

}
