-- Dominio: Sensors — relacion N:M sensor <-> variable.
-- Fuente: 06-data/domain/ms-sensor-management.md
CREATE TABLE sensors.sensor_variable (
    sensor_id   UUID NOT NULL,
    variable_id UUID NOT NULL,
    PRIMARY KEY (sensor_id, variable_id)
);
