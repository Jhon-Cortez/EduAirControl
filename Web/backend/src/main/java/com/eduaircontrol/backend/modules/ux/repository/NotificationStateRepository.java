package com.eduaircontrol.backend.modules.ux.repository;

import com.eduaircontrol.backend.modules.ux.entity.NotificationState;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationStateRepository extends JpaRepository<NotificationState, NotificationState.Pk> {
    List<NotificationState> findByUserId(UUID userId);

    List<NotificationState> findByUserIdAndAlertIdIn(UUID userId, Collection<UUID> alertIds);

    void deleteByUserIdAndAlertIdIn(UUID userId, Collection<UUID> alertIds);
}
