-- Dominio: Monitoring & Data Analysis — analisis historico por periodo.
-- Fuente: 06-data/domain/ms-environment-monitoring.md
-- educational_environment_id: referencia logica al dominio classrooms.
-- analysis_status_id: referencia logica (sin tabla en el modelo canonico).
-- requested_by: referencia logica al servicio IAM.
CREATE TABLE monitoring.environmental_analysis (
    environmental_analysis_id  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    educational_environment_id UUID NOT NULL,
    period_start TIMESTAMPTZ NOT NULL,
    period_end   TIMESTAMPTZ NOT NULL,
    analysis_status_id UUID NOT NULL,
    requested_by UUID,
    computed_at  TIMESTAMPTZ,
    CONSTRAINT ck_environmental_analysis_period CHECK (period_start < period_end)
);
