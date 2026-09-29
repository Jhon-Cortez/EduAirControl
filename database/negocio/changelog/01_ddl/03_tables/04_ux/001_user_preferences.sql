-- Dominio: UX — fuente literal: 06-data/domain/ms-user-experience.md
-- user_id: referencia logica al servicio IAM.
CREATE TABLE ux.user_preferences (
    preference_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    language VARCHAR(10),
    color_theme VARCHAR(20),
    notifications_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    time_zone VARCHAR(100)
);
