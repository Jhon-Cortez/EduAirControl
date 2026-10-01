package com.eduaircontrol.backend.modules.ux.application;

import com.eduaircontrol.backend.modules.ux.entity.NotificationState;
import com.eduaircontrol.backend.modules.ux.repository.NotificationStateRepository;
import com.eduaircontrol.backend.shared.contract.NotificationStatePort;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationStateService implements NotificationStatePort {

    private final NotificationStateRepository notificationStateRepository;

    @Override
    @Transactional(readOnly = true)
    public Set<UUID> readAlertIds(UUID userId) {
        if (userId == null) {
            return Set.of();
        }
        return notificationStateRepository.findByUserId(userId).stream()
                .map(NotificationState::getAlertId)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void markRead(UUID userId, UUID alertId) {
        if (userId == null || alertId == null) {
            return;
        }
        notificationStateRepository.findById(new NotificationState.Pk(userId, alertId))
                .orElseGet(() -> notificationStateRepository.save(
                        new NotificationState(userId, alertId, Instant.now())));
    }

    @Override
    @Transactional
    public void markAllRead(UUID userId, Set<UUID> alertIds) {
        if (userId == null || alertIds == null || alertIds.isEmpty()) {
            return;
        }
        alertIds.forEach(alertId -> markRead(userId, alertId));
    }

    @Override
    @Transactional(readOnly = true)
    public Instant readAt(UUID userId, UUID alertId) {
        if (userId == null || alertId == null) {
            return null;
        }
        return notificationStateRepository.findById(new NotificationState.Pk(userId, alertId))
                .map(NotificationState::getReadAt)
                .orElse(null);
    }

    /** Carga masiva para el listado de notificaciones. */
    @Transactional(readOnly = true)
    public Map<UUID, Instant> readAtByAlerts(UUID userId, Set<UUID> alertIds) {
        if (userId == null || alertIds == null || alertIds.isEmpty()) {
            return Map.of();
        }
        return notificationStateRepository.findByUserIdAndAlertIdIn(userId, alertIds).stream()
                .collect(Collectors.toMap(NotificationState::getAlertId, NotificationState::getReadAt,
                        (first, second) -> first));
    }
}
