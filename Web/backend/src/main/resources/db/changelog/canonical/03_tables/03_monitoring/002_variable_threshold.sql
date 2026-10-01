-- Dominio: Monitoring & Data Analysis — fuente: 06-data/domain/ms-environment-monitoring.md
-- variable_id / environment_type_id: referencias logicas a dominios ajenos (sensors, classrooms).
-- severity_id: FK fisica intra-dominio (04_alter).
CREATE TABLE monitoring.variable_threshold (
    variable_threshold_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    variable_id         UUID NOT NULL,
    environment_type_id UUID NOT NULL,
    min_value           NUMERIC(12,4),
    max_value           NUMERIC(12,4),
    severity_id         UUID NOT NULL,
    valid_from          DATE NOT NULL,
    valid_to            DATE,
    CONSTRAINT ck_variable_threshold_values
        CHECK (min_value IS NULL OR max_value IS NULL OR min_value < max_value),
    CONSTRAINT ck_variable_threshold_validity
        CHECK (valid_to IS NULL OR valid_from < valid_to)
);
