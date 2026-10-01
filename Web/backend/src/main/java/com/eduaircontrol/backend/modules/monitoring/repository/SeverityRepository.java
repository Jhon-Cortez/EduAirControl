package com.eduaircontrol.backend.modules.monitoring.repository;

import com.eduaircontrol.backend.modules.monitoring.entity.AlertStatus;
import com.eduaircontrol.backend.modules.monitoring.entity.Severity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeverityRepository extends JpaRepository<Severity, UUID> {
    Optional<Severity> findByCode(String code);
}

