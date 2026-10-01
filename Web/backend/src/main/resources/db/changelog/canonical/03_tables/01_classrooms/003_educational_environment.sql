-- Dominio: Classrooms — nombre canonico singular (00-governance/documentation-rules.md).
-- FKs (campus_id, environment_type_id) se agregan en 04_alter.
-- Fuente: 06-data/domain/ms-classroom-management.md
CREATE TABLE classrooms.educational_environment (
    educational_environment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    campus_id           UUID NOT NULL,
    code                VARCHAR(30)  NOT NULL,
    name                VARCHAR(120) NOT NULL,
    environment_type_id UUID NOT NULL,
    floor               INTEGER,
    area_m2             NUMERIC(8,2),
    occupancy_capacity  INTEGER,
    status              VARCHAR(30)  NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at          TIMESTAMPTZ,
    CONSTRAINT uq_educational_environment_campus_code UNIQUE (campus_id, code)
);
