package com.eduaircontrol.backend.modules.sensors.repository;

import com.eduaircontrol.backend.modules.sensors.entity.SensorStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorStatusRepository extends JpaRepository<SensorStatus, UUID> {
    Optional<SensorStatus> findByCode(String code);
}
