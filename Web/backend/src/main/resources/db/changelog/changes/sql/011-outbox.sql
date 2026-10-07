-- Outbox transaccional (ADR-007).
--
-- Esquema propio de infraestructura, no de dominio: el patron es transversal y
-- lo usara todo dominio del monolito que necesite publicar eventos. Por eso no
-- vive en monitoring ni en sensors, aunque hoy solo monitoring lo use.
--
-- La garantia que aporta es simple y es lo que importa: la fila del outbox se
-- escribe en la MISMA transaccion que el cambio de negocio. O se guardan ambos,
-- o no se guarda ninguno. No existe el estado intermedio "medicion guardada pero
-- evento perdido", que es el fallo clasico de publicar directo desde el servicio.
CREATE SCHEMA IF NOT EXISTS outbox;

CREATE TABLE outbox.outbox_event (
    event_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_type     VARCHAR(80)  NOT NULL,
    aggregate_type VARCHAR(60)  NOT NULL,
    aggregate_id   VARCHAR(60)  NOT NULL,
    routing_key    VARCHAR(80)  NOT NULL,
    -- text y no jsonb: el relay solo reenvia el cuerpo tal cual, nunca lo
    -- consulta por SQL. Guardarlo como texto evita la doble conversion
    -- (objeto -> Map -> JSON) que exigiria el mapeo jsonb de Hibernate.
    -- Para inspeccionarlo basta un payload::jsonb.
    payload        TEXT         NOT NULL,
    occurred_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    -- NULL = pendiente de publicar. El relay solo mira estos.
    published_at   TIMESTAMPTZ,
    attempts       INTEGER      NOT NULL DEFAULT 0,
    last_error     TEXT
);

-- Indice parcial: el relay pregunta constantly por lo no publicado, y la tabla
-- acumula todos los historicos ya enviados. Un indice total desperdiciaria
-- paginas; uno parcial lo resuelve en indice constante.
CREATE INDEX ix_outbox_event_pending
    ON outbox.outbox_event (occurred_at)
    WHERE published_at IS NULL;

-- Traza por agregado: permite reconstruir "que le Paso a este ambiente".
CREATE INDEX ix_outbox_event_aggregate
    ON outbox.outbox_event (aggregate_type, aggregate_id, occurred_at);
