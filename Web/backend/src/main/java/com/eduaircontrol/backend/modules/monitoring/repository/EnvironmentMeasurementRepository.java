package com.eduaircontrol.backend.modules.monitoring.repository;

import com.eduaircontrol.backend.modules.monitoring.entity.EnvironmentMeasurement;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnvironmentMeasurementRepository extends JpaRepository<EnvironmentMeasurement, UUID> {

    /**
     * Ultima lectura por ambiente y variable (ambientes activos).
     */
    @Query(value = """
            SELECT DISTINCT ON (si.educational_environment_id, m.variable_id)
                   si.educational_environment_id AS environment_id,
                   m.variable_id,
                   v.code AS variable_code,
                   m.measured_value,
                   m.measured_at
            FROM monitoring.environment_measurement m
            JOIN sensors.sensor_installation si
                ON si.sensor_installation_id = m.sensor_installation_id
            JOIN sensors.variable v ON v.variable_id = m.variable_id
            WHERE si.removed_at IS NULL
              AND (:ids IS NULL OR si.educational_environment_id IN (:ids))
            ORDER BY si.educational_environment_id, m.variable_id, m.measured_at DESC
            """, nativeQuery = true)
    List<LatestRow> findLatestByEnvironment(@Param("ids") List<UUID> ids);

    @Query(value = """
            SELECT max(m.measured_at)
            FROM monitoring.environment_measurement m
            JOIN sensors.sensor_installation si
                ON si.sensor_installation_id = m.sensor_installation_id
            WHERE si.removed_at IS NULL
              AND (:environmentId IS NULL OR si.educational_environment_id = :environmentId)
            """, nativeQuery = true)
    Instant findLatestMeasuredAt(@Param("environmentId") UUID environmentId);

    @Query(value = """
            SELECT date_trunc(CAST(:unit AS text), m.measured_at) AS bucket,
                   avg(m.measured_value) AS avg_value,
                   count(*) AS samples
            FROM monitoring.environment_measurement m
            JOIN sensors.sensor_installation si
                ON si.sensor_installation_id = m.sensor_installation_id
            JOIN sensors.variable v ON v.variable_id = m.variable_id
            WHERE si.removed_at IS NULL
              AND v.code = :variable
              AND m.measured_at >= :from
              AND (:environmentId IS NULL OR si.educational_environment_id = :environmentId)
            GROUP BY 1
            ORDER BY 1
            """, nativeQuery = true)
    java.util.List<SeriesRow> findSeries(@Param("unit") String unit,
                                         @Param("variable") String variable,
                                         @Param("from") java.time.Instant from,
                                         @Param("environmentId") UUID environmentId);

    boolean existsBySensorInstallationIdAndVariableIdAndMeasuredAt(
            UUID sensorInstallationId, UUID variableId, java.time.Instant measuredAt);

    interface SeriesRow {
        java.sql.Timestamp getBucket();

        java.math.BigDecimal getAvg_value();

        Long getSamples();
    }

    interface LatestRow {
        UUID getEnvironment_id();

        UUID getVariable_id();

        String getVariable_code();

        java.math.BigDecimal getMeasured_value();

        Instant getMeasured_at();
    }
}
