-- Seed de desarrollo: serie historica de mediciones cada 3 horas durante 365 dias
-- (32 instalaciones x 2921 puntos ~ 93k filas). Cubre los 4 periodos del dashboard.
-- Contexto: solo se aplica con contexts=dev.

INSERT INTO monitoring.environment_measurement
    (environment_measurement_id, sensor_installation_id, variable_id, measured_value, measured_at, quality_flag_id)
SELECT
    gen_random_uuid(),
    si.sensor_installation_id,
    sv.variable_id,
    CASE v.code
        WHEN 'temperature' THEN round((
            21.0
            + 3.2 * sin(2 * pi() * extract(hour from ts) / 24.0)
            + 1.1 * sin(2 * pi() * extract(doy from ts) / 365.0)
            + (random() - 0.5) * 1.4
        )::numeric, 1)
        WHEN 'humidity' THEN round((
            50.0
            + 9.0 * sin(2 * pi() * extract(hour from ts) / 24.0 + 1.2)
            + 6.0 * sin(2 * pi() * extract(doy from ts) / 365.0)
            + (random() - 0.5) * 5.0
        )::numeric, 1)
        WHEN 'co2' THEN round((
            560.0
            + CASE WHEN extract(hour from ts) BETWEEN 7 AND 18 THEN 330.0 ELSE 90.0 END
            + 90.0 * sin(2 * pi() * extract(dow from ts) / 7.0)
            + (random() - 0.5) * 160.0
        )::numeric, 1)
        WHEN 'noise' THEN round((
            44.0
            + CASE WHEN extract(hour from ts) BETWEEN 7 AND 18 THEN 16.0 ELSE 4.0 END
            + 5.0 * sin(2 * pi() * extract(dow from ts) / 7.0)
            + (random() - 0.5) * 9.0
        )::numeric, 1)
    END,
    ts,
    '00000000-0000-4000-8000-000000000031'
FROM sensors.sensor_installation si
JOIN sensors.sensor_variable sv ON sv.sensor_id = si.sensor_id
JOIN sensors.variable v ON v.variable_id = sv.variable_id
CROSS JOIN generate_series(
    date_trunc('day', now() - interval '365 days'),
    now(),
    interval '3 hours'
) AS ts
WHERE si.removed_at IS NULL;
