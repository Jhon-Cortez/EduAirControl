-- Dominio: Monitoring & Data Analysis — tabla de lookup de severidad.
-- Restaurada como tabla fisica (open item #5 de 06-data/normalization-assessment.md).
-- Fuente: 06-data/domain/ms-environment-monitoring.md
CREATE TABLE monitoring.severity (
    severity_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(20) NOT NULL UNIQUE,
    name        VARCHAR(40) NOT NULL,
    level       SMALLINT NOT NULL UNIQUE
);
