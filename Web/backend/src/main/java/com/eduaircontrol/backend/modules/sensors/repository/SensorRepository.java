package com.eduaircontrol.backend.modules.sensors.repository;

import com.eduaircontrol.backend.modules.sensors.entity.Sensor;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, UUID> {
    Optional<Sensor> findBySerialNumber(String serialNumber);
}
