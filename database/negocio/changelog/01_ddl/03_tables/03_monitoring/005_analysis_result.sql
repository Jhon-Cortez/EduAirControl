-- Dominio: Monitoring & Data Analysis — resultado agregado por analisis y variable.
-- Fuente: 06-data/domain/ms-environment-monitoring.md
-- variable_id: referencia logica al dominio sensors.
CREATE TABLE monitoring.analysis_result (
    analysis_result_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    environmental_analysis_id UUID NOT NULL,
    variable_id      UUID NOT NULL,
    min_value        NUMERIC(12,4) NOT NULL,
    max_value        NUMERIC(12,4) NOT NULL,
    avg_value        NUMERIC(12,4) NOT NULL,
    sample_count     INTEGER NOT NULL,
    exceedance_count INTEGER NOT NULL,
    CONSTRAINT uq_analysis_result_period_variable
        UNIQUE (environmental_analysis_id, variable_id)
);
