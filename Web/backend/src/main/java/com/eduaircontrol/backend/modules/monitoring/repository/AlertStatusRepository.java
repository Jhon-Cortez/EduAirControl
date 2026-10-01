package com.eduaircontrol.backend.modules.monitoring.repository;

import com.eduaircontrol.backend.modules.monitoring.entity.AlertStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertStatusRepository extends JpaRepository<AlertStatus, UUID> {
    Optional<AlertStatus> findByCode(String code);
}
