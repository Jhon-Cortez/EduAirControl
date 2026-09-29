-- Dominio: Sensors — historial de instalaciones. Fuente: 06-data/domain/ms-sensor-management.md
-- educational_environment_id: referencia logica al dominio classrooms (entre dominios no hay FK).
CREATE TABLE sensors.sensor_installation (
    sensor_installation_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sensor_id                    UUID NOT NULL,
    educational_environment_id   UUID NOT NULL,
    installed_at                 TIMESTAMPTZ NOT NULL,
    removed_at                   TIMESTAMPTZ
);
