-- Seed de desarrollo: ajusta las ultimas lecturas para que haya estados variados
-- (normal / advertencia / alerta) y genera las alertas abiertas que alimentan
-- la campana de notificaciones. Contexto: solo se aplica con contexts=dev.

-- 1) Ultima lectura por ambiente+variable.
CREATE TEMP TABLE tmp_latest_measurement ON COMMIT DROP AS
SELECT DISTINCT ON (si.educational_environment_id, m.variable_id)
    m.environment_measurement_id,
    m.variable_id,
    si.educational_environment_id
FROM monitoring.environment_measurement m
JOIN sensors.sensor_installation si
    ON si.sensor_installation_id = m.sensor_installation_id
ORDER BY si.educational_environment_id, m.variable_id, m.measured_at DESC;

-- 2) Valores finales forzados: 209-3 CO2 critico, Auditorio ruido critico,
--    209-2 temperatura en advertencia, Biblioteca humedad en advertencia.
UPDATE monitoring.environment_measurement m
SET measured_value = CASE l.variable_id
        WHEN '00000000-0000-4000-8000-000000000073' THEN 1150.0
        ELSE 0
    END
FROM tmp_latest_measurement l
WHERE m.environment_measurement_id = l.environment_measurement_id
  AND l.educational_environment_id = '00000000-0000-4000-8000-000000000203'
  AND l.variable_id = '00000000-0000-4000-8000-000000000073';

UPDATE monitoring.environment_measurement m
SET measured_value = 78.0
FROM tmp_latest_measurement l
WHERE m.environment_measurement_id = l.environment_measurement_id
  AND l.educational_environment_id = '00000000-0000-4000-8000-000000000207'
  AND l.variable_id = '00000000-0000-4000-8000-000000000074';

UPDATE monitoring.environment_measurement m
SET measured_value = 27.4
FROM tmp_latest_measurement l
WHERE m.environment_measurement_id = l.environment_measurement_id
  AND l.educational_environment_id = '00000000-0000-4000-8000-000000000202'
  AND l.variable_id = '00000000-0000-4000-8000-000000000071';

UPDATE monitoring.environment_measurement m
SET measured_value = 71.0
FROM tmp_latest_measurement l
WHERE m.environment_measurement_id = l.environment_measurement_id
  AND l.educational_environment_id = '00000000-0000-4000-8000-000000000206'
  AND l.variable_id = '00000000-0000-4000-8000-000000000072';

-- 3) Alertas abiertas por cada lectura que excede el umbral CRITICAL del tipo.
INSERT INTO monitoring.environment_alert
    (environment_alert_id, educational_environment_id, variable_id,
     variable_threshold_id, triggering_measurement_id, alert_status_id,
     raised_at, created_at, updated_at)
SELECT
    gen_random_uuid(),
    l.educational_environment_id,
    l.variable_id,
    th.variable_threshold_id,
    l.environment_measurement_id,
    '00000000-0000-4000-8000-000000000051',
    now() - (row_number() OVER (ORDER BY l.educational_environment_id) || ' minutes')::interval,
    now(),
    now()
FROM tmp_latest_measurement l
JOIN classrooms.educational_environment e
    ON e.educational_environment_id = l.educational_environment_id
JOIN monitoring.variable_threshold th
    ON th.environment_type_id = e.environment_type_id
   AND th.variable_id = l.variable_id
   AND th.educational_environment_id IS NULL
   AND th.severity_id = '00000000-0000-4000-8000-000000000063'
JOIN monitoring.environment_measurement m
    ON m.environment_measurement_id = l.environment_measurement_id
WHERE (th.max_value IS NOT NULL AND m.measured_value > th.max_value)
   OR (th.min_value IS NOT NULL AND m.measured_value < th.min_value);

-- 4) Alertas historicas ya resuelidas (para poblar el listado de notificaciones).
INSERT INTO monitoring.environment_alert
    (environment_alert_id, educational_environment_id, variable_id,
     variable_threshold_id, triggering_measurement_id, alert_status_id,
     raised_at, acknowledged_at, resolved_at, created_at, updated_at)
SELECT
    gen_random_uuid(),
    l.educational_environment_id,
    l.variable_id,
    th.variable_threshold_id,
    l.environment_measurement_id,
    '00000000-0000-4000-8000-000000000053',
    now() - interval '3 days',
    now() - interval '3 days' + interval '10 minutes',
    now() - interval '3 days' + interval '2 hours',
    now() - interval '3 days',
    now() - interval '3 days'
FROM tmp_latest_measurement l
JOIN classrooms.educational_environment e
    ON e.educational_environment_id = l.educational_environment_id
JOIN monitoring.variable_threshold th
    ON th.environment_type_id = e.environment_type_id
   AND th.variable_id = l.variable_id
   AND th.educational_environment_id IS NULL
   AND th.severity_id = '00000000-0000-4000-8000-000000000063'
JOIN monitoring.environment_measurement m
    ON m.environment_measurement_id = l.environment_measurement_id
WHERE (th.max_value IS NOT NULL AND m.measured_value > th.max_value)
   OR (th.min_value IS NOT NULL AND m.measured_value < th.min_value)
ORDER BY l.educational_environment_id, l.variable_id
LIMIT 3;

DROP TABLE tmp_latest_measurement;
