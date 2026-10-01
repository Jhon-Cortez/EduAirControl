package com.eduaircontrol.backend.modules.sensors.repository;

import com.eduaircontrol.backend.modules.sensors.entity.Device;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, UUID> {
    Optional<Device> findByMacAddress(String macAddress);

    Optional<Device> findByApiKeyPrefix(String apiKeyPrefix);
}
