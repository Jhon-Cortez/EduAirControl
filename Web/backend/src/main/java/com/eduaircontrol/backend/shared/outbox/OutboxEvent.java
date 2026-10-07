package com.eduaircontrol.backend.shared.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento pendiente de publicar hacia otro servicio.
 *
 * <p>Se escribe dentro de la transaccion de negocio; el relay la marca como
 * publicada solo cuando el broker confirma. Si el relay se cae a mitad de camino,
 * el evento sigue pendiente y se reintenta en la siguiente pasada.
 *
 * <p>El payload se guarda como texto y no como jsonb a proposito: el relay solo
 * necesita reenviarlo tal cual, y almacenarlo como texto evita depender del
 * mapeo de tipos JSON de Hibernate para un dato que nunca se consulta por SQL.
 */
@Entity
@Table(name = "outbox_event", schema = "outbox")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @Column(name = "event_id")
    private UUID id;

    @Column(name = "event_type", nullable = false, length = 80)
    private String eventType;

    @Column(name = "aggregate_type", nullable = false, length = 60)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, length = 60)
    private String aggregateId;

    @Column(name = "routing_key", nullable = false, length = 80)
    private String routingKey;

    @Column(nullable = false, columnDefinition = "text")
    private String payload;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "last_error", columnDefinition = "text")
    private String lastError;

    public boolean isPending() {
        return publishedAt == null;
    }
}
