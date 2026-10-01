package com.eduaircontrol.backend.modules.monitoring.repository;

import com.eduaircontrol.backend.modules.monitoring.entity.QualityFlag;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QualityFlagRepository extends JpaRepository<QualityFlag, UUID> {
    Optional<QualityFlag> findByCode(String code);
}
