package com.eduaircontrol.backend.modules.sensors.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * Consulta en crudo del panel de sensores: sensor + variable + su ultima
 * instalacion (activa o retirada). Se hace con JDBC porque es una proyeccion,
 * no una entidad. `installed` distingue el sensor dado de baja (se conserva en
 * el listado para poder reactivarlo) del que simplemente deja de reportar.
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
                   v.variable_id,
                   COALESCE(i.removed_at IS NULL, false) AS installed
            FROM sensors.sensor s
            LEFT JOIN LATERAL (
                SELECT sensor_id, educational_environment_id, removed_at
                FROM sensors.sensor_installation
                WHERE sensor_id = s.sensor_id
                ORDER BY (removed_at IS NULL) DESC, installed_at DESC
                LIMIT 1
            ) i ON TRUE
            JOIN sensors.sensor_variable sv ON sv.sensor_id = s.sensor_id
            JOIN sensors.variable v ON v.variable_id = sv.variable_id
            WHERE (?::uuid IS NULL OR i.educational_environment_id = ?::uuid)
            ORDER BY s.serial_number
            """;

    private static final RowMapper<ActiveSensorRow> MAPPER = (rs, rowNum) -> new ActiveSensorRow(
            rs.getString("serial_number"),
            rs.getObject("sensor_id", UUID.class),
            rs.getObject("educational_environment_id", UUID.class),
            rs.getTimestamp("last_seen_at") != null ? rs.getTimestamp("last_seen_at").toInstant() : null,
            rs.getString("code"),
            rs.getObject("variable_id", UUID.class),
            rs.getBoolean("installed"));

    private final JdbcTemplate jdbcTemplate;

    public List<ActiveSensorRow> findRows(UUID environmentId) {
        return jdbcTemplate.query(SQL, MAPPER, environmentId, environmentId);
    }

    public record ActiveSensorRow(String serialNumber, UUID sensorId, UUID environmentId,
                                  Instant lastSeenAt, String variableCode, UUID variableId,
                                  boolean installed) {
    }
}
