package com.eduaircontrol.backend.modules.device.repository;

import com.eduaircontrol.backend.modules.device.entity.Device;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByMacAddress(String macAddress);
    boolean existsByMacAddress(String macAddress);
}
