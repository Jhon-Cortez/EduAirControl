-- Dominio: Monitoring & Data Analysis (alertas incluidas por decision de implementacion).
-- Fuente base: 06-data/domain/ms-alert.md, ajustada a la particion por dominios:
--   * educational_environment_id / variable_id: referencias logicas (entre dominios).
--   * alert_status_id: referencia logica al catalogo excluido alert_status.
--   * acknowledged_by: referencia logica al servicio IAM.
--   * FKs intra-dominio (variable_threshold, triggering_measurement): 04_alter.
CREATE TABLE monitoring.environment_alert (
    environment_alert_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    educational_environment_id UUID NOT NULL,
    variable_id                UUID NOT NULL,
    variable_threshold_id      UUID NOT NULL,
    triggering_measurement_id  UUID NOT NULL,
    alert_status_id            UUID NOT NULL,

    raised_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    acknowledged_at TIMESTAMPTZ,
    acknowledged_by UUID,
    resolved_at     TIMESTAMPTZ,

    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ
);
