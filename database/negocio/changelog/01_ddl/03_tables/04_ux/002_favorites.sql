-- Dominio: UX — fuente literal: 06-data/domain/ms-user-experience.md
-- user_id / classroom_id / variable_id: referencias logicas (IAM, classrooms, sensors).
CREATE TABLE ux.favorites (
    favorite_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    classroom_id UUID,
    variable_id UUID,
    added_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_favorite_target
        CHECK (
            classroom_id IS NOT NULL
            OR variable_id IS NOT NULL
        )
);
