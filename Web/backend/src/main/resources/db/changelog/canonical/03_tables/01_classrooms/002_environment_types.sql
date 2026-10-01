-- Dominio: Classrooms — tabla de negocio (define que umbral de variable_threshold aplica),
-- no es catalogo de parametrizacion. Fuente: 06-data/domain/ms-classroom-management.md
CREATE TABLE classrooms.environment_types (
    environment_type_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code                 VARCHAR(30) NOT NULL UNIQUE,
    name                 VARCHAR(80) NOT NULL UNIQUE,
    description          VARCHAR(255),
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at           TIMESTAMPTZ
);
