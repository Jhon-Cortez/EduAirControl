-- Seed de desarrollo: 1 sensor por ambiente y variable (32 sensores),
-- su instalacion y la relacion N:M sensor_variable.
-- UUIDs deterministas: 3xx = sensor, 4xx = instalacion (ver 101-seed-dev-sensors.yaml).
-- Contexto: solo se aplica con contexts=dev.

WITH vars AS (
  SELECT variable_id, code,
         row_number() OVER (ORDER BY code) AS var_rn
  FROM sensors.variable
),
pairs AS (
  SELECT e.educational_environment_id, v.variable_id, v.code AS var_code,
         row_number() OVER (ORDER BY e.code, v.code) AS rn
  FROM classrooms.educational_environment e
  CROSS JOIN vars v
)
INSERT INTO sensors.sensor (sensor_id, serial_number, sensor_model_id, sensor_status_id, last_seen_at, created_at, updated_at)
SELECT
  ('00000000-0000-4000-8000-' || lpad((300 + rn)::text, 12, '0'))::uuid,
  'EA-' || e.code || '-' || upper(substr(p.var_code, 1, 1)),
  CASE p.var_code WHEN 'co2' THEN '00000000-0000-4000-8000-000000000012'
                  WHEN 'noise' THEN '00000000-0000-4000-8000-000000000013'
                  ELSE '00000000-0000-4000-8000-000000000011' END::uuid,
  '00000000-0000-4000-8000-000000000021'::uuid,
  now(), now(), now()
FROM pairs p
JOIN classrooms.educational_environment e
  ON e.educational_environment_id = p.educational_environment_id;

WITH pairs AS (
  SELECT e.educational_environment_id, v.variable_id,
         row_number() OVER (ORDER BY e.code, v.code) AS rn
  FROM classrooms.educational_environment e
  CROSS JOIN sensors.variable v
)
INSERT INTO sensors.sensor_installation
  (sensor_installation_id, sensor_id, educational_environment_id, installed_at, removed_at)
SELECT
  ('00000000-0000-4000-8000-' || lpad((400 + rn)::text, 12, '0'))::uuid,
  ('00000000-0000-4000-8000-' || lpad((300 + rn)::text, 12, '0'))::uuid,
  educational_environment_id,
  now() - interval '180 days',
  NULL
FROM pairs;

WITH pairs AS (
  SELECT row_number() OVER (ORDER BY e.code, v.code) AS rn, v.variable_id
  FROM classrooms.educational_environment e
  CROSS JOIN sensors.variable v
)
INSERT INTO sensors.sensor_variable (sensor_id, variable_id)
SELECT ('00000000-0000-4000-8000-' || lpad((300 + rn)::text, 12, '0'))::uuid, variable_id
FROM pairs;
