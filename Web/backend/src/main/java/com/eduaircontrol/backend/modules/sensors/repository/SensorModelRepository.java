package com.eduaircontrol.backend.modules.sensors.repository;

import com.eduaircontrol.backend.modules.sensors.entity.SensorModel;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorModelRepository extends JpaRepository<SensorModel, UUID> {
    Optional<SensorModel> findByCode(String code);
}
