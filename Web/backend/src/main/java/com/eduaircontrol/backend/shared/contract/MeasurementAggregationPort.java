package com.eduaircontrol.backend.shared.contract;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Contrato de agregacion de mediciones (modulo monitoring, duenno de
 * {@code monitoring.environment_measurement}).
 *
 * <p>El modulo de analisis lo consume para materializar una ventana en resultados
 * por variable sin acceder directamente a las tablas del duenno (ADR-003), lo que
 * ademas mantiene la frontera interna que exige ADR-002.
 */
public interface MeasurementAggregationPort {

    /**
     * Agrega las mediciones del periodo [start, end) por variable para un ambiente.
     * El umbral de advertencia se aplica en el conteo de excedencias usando el
     * umbral vigente del ambiente; un rango nulo o sin limite no genera excedencias.
     */
    List<Aggregate> aggregate(UUID environmentId, Instant start, Instant end);

    /**
     * Agregacion de una variable dentro del periodo.
     *
     * @param variableId     variable ambiental agregada
     * @param variableCode   codigo estable de la variable (TEMP, HUM, CO2, NOISE)
     * @param minValue       menor muestra del periodo
     * @param maxValue       mayor muestra del periodo
     * @param avgValue       promedio de las muestras del periodo
     * @param sampleCount    cantidad de muestras agregadas
     * @param exceedanceCount muestras fuera del umbral de advertencia vigente
     */
    record Aggregate(
            UUID variableId,
            String variableCode,
            BigDecimal minValue,
            BigDecimal maxValue,
            BigDecimal avgValue,
            long sampleCount,
            long exceedanceCount) {
    }
}