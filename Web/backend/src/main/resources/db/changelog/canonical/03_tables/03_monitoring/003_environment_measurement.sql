-- Dominio: Monitoring & Data Analysis — medicion atomica (1 fila = instalacion + variable + instante).
-- Fuente: 06-data/domain/ms-environment-monitoring.md
-- sensor_installation_id / variable_id: referencias logicas al dominio sensors.
-- quality_flag_id: referencia logica (sin tabla en el modelo canonico).
CREATE TABLE monitoring.environment_measurement (
    environment_measurement_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sensor_installation_id UUID NOT NULL,
    variable_id    UUID NOT NULL,
    measured_value NUMERIC(12,4) NOT NULL,
    measured_at    TIMESTAMPTZ NOT NULL,
    quality_flag_id UUID NOT NULL
);
