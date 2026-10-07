package com.eduaircontrol.backend.shared.outbox;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/**
 * Encola un evento para su publicacion posterior.
 *
 * <p>Se invoca desde el servicio de dominio, dentro de la transaccion ya abierta:
 * el append solo anade una fila, no publica nada. Si la transaccion de negocio
 * falla, la fila tampoco se guarda.
 */
@Component
@RequiredArgsConstructor
public class OutboxWriter {

    private final OutboxRepository repository;
    private final JsonMapper jsonMapper;

    /**
     * @param payload objeto de dominio a serializar como JSON del evento
     * @return id del evento encolado, util para trazas
     */
    public UUID append(String eventType, String aggregateType, String aggregateId,
                       String routingKey, Object payload) {
        return append(eventType, aggregateType, aggregateId, routingKey, payload, Instant.now());
    }

    public UUID append(String eventType, String aggregateType, String aggregateId,
                       String routingKey, Object payload, Instant occurredAt) {
        OutboxEvent event = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .eventType(eventType)
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .routingKey(routingKey)
                .payload(serialize(payload))
                .occurredAt(occurredAt)
                .attempts(0)
                .build();
        repository.save(event);
        return event.getId();
    }

    private String serialize(Object payload) {
        // Si el payload no se puede serializar, el evento no sirve de nada: conviene
        // fallar la transaccion de negocio y no encolar basura a la espera.
        return jsonMapper.writeValueAsString(payload);
    }
}