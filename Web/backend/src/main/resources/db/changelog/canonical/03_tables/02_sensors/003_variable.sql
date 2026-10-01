-- Dominio: Sensors — fuente: 06-data/domain/ms-sensor-management.md
-- measurement_unit_id: referencia logica al catalogo excluido measurement_units.
CREATE TABLE sensors.variable (
    variable_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code                VARCHAR(30) NOT NULL UNIQUE,
    name                VARCHAR(80) NOT NULL,
    measurement_unit_id UUID NOT NULL,
    description         VARCHAR(255),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);
