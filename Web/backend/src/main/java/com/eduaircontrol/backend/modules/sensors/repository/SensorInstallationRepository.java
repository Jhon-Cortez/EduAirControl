package com.eduaircontrol.backend.modules.sensors.repository;

import com.eduaircontrol.backend.modules.sensors.entity.SensorInstallation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SensorInstallationRepository extends JpaRepository<SensorInstallation, UUID> {

    @Query("""
            select i from SensorInstallation i
            where i.removedAt is null
              and (:environmentId is null or i.educationalEnvironmentId = :environmentId)
            order by i.installedAt desc
            """)
    List<SensorInstallation> findActive(@Param("environmentId") UUID environmentId);

    Optional<SensorInstallation> findBySensorIdAndRemovedAtIsNull(UUID sensorId);

    java.util.List<SensorInstallation> findBySensorId(UUID sensorId);

    Optional<SensorInstallation> findBySensorIdAndEducationalEnvironmentIdAndRemovedAtIsNull(
            UUID sensorId, UUID educationalEnvironmentId);

    List<SensorInstallation> findAllByEducationalEnvironmentIdAndRemovedAtIsNull(UUID educationalEnvironmentId);
}
