package com.eduaircontrol.backend.modules.monitoring.repository;

import com.eduaircontrol.backend.shared.contract.MeasurementAggregationPort;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Agregacion de mediciones por variable para el modulo de analisis.
 *
 * <p>Es una proyeccion, no una entidad, por eso se resuelve con JDBC crudo (mismo
 * criterio que {@code SensorRowDao}). Los umbrales de advertencia llegan como arrays
 * paralelos y se cruzan con {@code unnest}, de modo que una sola consulta resuelve
 * minimo, maximo, promedio, cantidad de muestras y excedencias por variable.
 */
@Repository
public class MeasurementAggregationDao {

    private static final String SQL = """
            SELECT v.variable_id,
                   v.code AS variable_code,
                   min(m.measured_value) AS min_value,
                   max(m.measured_value) AS max_value,
                   avg(m.measured_value) AS avg_value,
                   count(*) AS sample_count,
                   count(*) FILTER (
                       WHERE (t.min_value IS NULL OR m.measured_value < t.min_value)
                          OR (t.max_value IS NULL OR m.measured_value > t.max_value)
                   ) AS exceedance_count
            FROM monitoring.environment_measurement m
            JOIN sensors.sensor_installation si
                ON si.sensor_installation_id = m.sensor_installation_id
            JOIN sensors.variable v ON v.variable_id = m.variable_id
            LEFT JOIN unnest(CAST(? AS uuid[]), CAST(? AS numeric[]), CAST(? AS numeric[]))
                       AS t(variable_id, min_value, max_value)
                   ON t.variable_id = v.variable_id
            WHERE si.removed_at IS NULL
              AND si.educational_environment_id = ?
              AND m.measured_at >= ?
              AND m.measured_at < ?
            GROUP BY v.variable_id, v.code
            ORDER BY v.code
            """;

    private final JdbcTemplate jdbcTemplate;

    public MeasurementAggregationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * @param ranges umbral de advertencia vigente por variable; puede estar vacio, en
     *               cuyo caso ninguna muestra se considera excedencia
     */
    public List<MeasurementAggregationPort.Aggregate> aggregate(
            UUID environmentId,
            Instant start,
            Instant end,
            Map<UUID, ThresholdRange> ranges) {
        UUID[] variableIds = ranges.keySet().toArray(new UUID[0]);
        BigDecimal[] mins = new BigDecimal[ranges.size()];
        BigDecimal[] maxs = new BigDecimal[ranges.size()];
        int index = 0;
        for (Map.Entry<UUID, ThresholdRange> entry : ranges.entrySet()) {
            ThresholdRange range = entry.getValue();
            mins[index] = range == null ? null : range.min();
            maxs[index] = range == null ? null : range.max();
            index++;
        }

        List<Row> rows = jdbcTemplate.query(con -> {
            PreparedStatement statement = con.prepareStatement(SQL);
            statement.setArray(1, con.createArrayOf("uuid", variableIds));
            statement.setArray(2, con.createArrayOf("numeric", toObjects(mins)));
            statement.setArray(3, con.createArrayOf("numeric", toObjects(maxs)));
            statement.setObject(4, environmentId);
            statement.setObject(5, java.sql.Timestamp.from(start));
            statement.setObject(6, java.sql.Timestamp.from(end));
            return statement;
        }, (rs, rowNum) -> new Row(
                rs.getObject("variable_id", UUID.class),
                rs.getString("variable_code"),
                rs.getBigDecimal("min_value"),
                rs.getBigDecimal("max_value"),
                rs.getBigDecimal("avg_value"),
                rs.getLong("sample_count"),
                rs.getLong("exceedance_count")));

        return rows.stream()
                .map(row -> new MeasurementAggregationPort.Aggregate(
                        row.variableId,
                        row.variableCode,
                        scale(row.minValue),
                        scale(row.maxValue),
                        scale(row.avgValue),
                        row.sampleCount,
                        row.exceedanceCount))
                .toList();
    }

    private Object[] toObjects(BigDecimal[] values) {
        Object[] boxed = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            boxed[i] = values[i];
        }
        return boxed;
    }

    private BigDecimal scale(BigDecimal value) {
        return value == null ? null : value.setScale(4, RoundingMode.HALF_UP);
    }

    private record Row(UUID variableId,
                       String variableCode,
                       BigDecimal minValue,
                       BigDecimal maxValue,
                       BigDecimal avgValue,
                       long sampleCount,
                       long exceedanceCount) {
    }

    /** Rango de advertencia vigente de una variable en el ambiente analizado. */
    public record ThresholdRange(BigDecimal min, BigDecimal max) {
    }
}