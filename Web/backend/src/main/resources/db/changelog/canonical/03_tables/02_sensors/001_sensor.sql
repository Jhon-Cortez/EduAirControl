-- Dominio: Sensors — fuente: 06-data/domain/ms-sensor-management.md
-- sensor_model_id / sensor_status_id: referencias logicas a catalogos excluidos
-- (sensor_models, sensor_statuses) — decision de implementacion, ver README.
CREATE TABLE sensors.sensor (
    sensor_id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    serial_number    VARCHAR(60) NOT NULL UNIQUE,
    sensor_model_id  UUID NOT NULL,
    sensor_status_id UUID NOT NULL,
    last_seen_at     TIMESTAMPTZ,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ
);
