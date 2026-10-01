package com.eduaircontrol.backend.shared.contract;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Estado de lectura de las alertas/notificaciones (modulo ux).
 */
public interface NotificationStatePort {

    Set<UUID> readAlertIds(UUID userId);

    void markRead(UUID userId, UUID alertId);

    void markAllRead(UUID userId, Set<UUID> alertIds);

    Instant readAt(UUID userId, UUID alertId);
}
