package com.eduaircontrol.backend.modules.sensors.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * Consulta en crudo del panel de sensores: instalacon activa + sensor + variable.
 * Se hace con JDBC porque es una proyeccion, no una entidad.
 */
@Repository
@RequiredArgsConstructor
public class SensorRowDao {

    private static final String SQL = """
            SELECT s.serial_number,
                   i.sensor_id,
                   i.educational_environment_id,
                   s.last_seen_at,
                   v.code,
                   v.variable_id
            FROM sensors.sensor_installation i
            JOIN sensors.sensor s ON s.sensor_id = i.sensor_id
            JOIN sensors.sensor_variable sv ON sv.sensor_id = i.sensor_id
            JOIN sensors.variable v ON v.variable_id = sv.variable_id
            WHERE i.removed_at IS NULL
              AND (?::uuid IS NULL OR i.educational_environment_id = ?::uuid)
            ORDER BY s.serial_number
            """;

    private static final RowMapper<ActiveSensorRow> MAPPER = (rs, rowNum) -> new ActiveSensorRow(
            rs.getString("serial_number"),
            rs.getObject("sensor_id", UUID.class),
            rs.getObject("educational_environment_id", UUID.class),
            rs.getTimestamp("last_seen_at") != null ? rs.getTimestamp("last_seen_at").toInstant() : null,
            rs.getString("code"),
            rs.getObject("variable_id", UUID.class));

    private final JdbcTemplate jdbcTemplate;

    public List<ActiveSensorRow> findActiveRows(UUID environmentId) {
        return jdbcTemplate.query(SQL, MAPPER, environmentId, environmentId);
    }

    public record ActiveSensorRow(String serialNumber, UUID sensorId, UUID environmentId,
                                  Instant lastSeenAt, String variableCode, UUID variableId) {
    }
}
