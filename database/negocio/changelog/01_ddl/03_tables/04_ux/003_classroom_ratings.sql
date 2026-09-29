-- Dominio: UX — fuente literal: 06-data/domain/ms-user-experience.md
-- user_id / classroom_id: referencias logicas (IAM, classrooms).
CREATE TABLE ux.classroom_ratings (
    rating_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    classroom_id UUID NOT NULL,
    score INTEGER NOT NULL,
    comment VARCHAR(1000),
    rated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_classroom_rating_score
        CHECK (score BETWEEN 1 AND 5)
);
