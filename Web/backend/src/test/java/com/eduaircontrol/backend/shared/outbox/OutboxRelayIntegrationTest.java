package com.eduaircontrol.backend.shared.outbox;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Verifica el relay del outbox contra un broker real.
 *
 * <p>Lo que importa aqui no es solo que el evento salga, sino que salga con el
 * header y la routing key que el consumidor de ms-environment-monitoring espera para enrutar y
 * deduplicar.
 */
class OutboxRelayIntegrationTest extends OutboxTestBase {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    private static final UUID ENVIRONMENT_ID = UUID.fromString("11111111-1111-4111-8111-111111111111");

    private UUID enqueue(UUID environmentId) {
        EnvironmentalDataRecorded body = EnvironmentalDataRecorded.at(
                environmentId,
                Instant.parse("2026-10-07T12:00:00Z"),
                new BigDecimal("21.5"),
                new BigDecimal("55.0"),
                new BigDecimal("612"),
                new BigDecimal("48.0"),
                UUID.fromString("22222222-2222-4222-8222-222222222222"));
        return writer.append(
                body.eventType(),
                body.aggregateType(),
                body.aggregateId().toString(),
                EnvironmentalDataRecorded.ROUTING_KEY,
                body,
                body.occurredAt());
    }

    @Test
    void publishesPendingEventToTheQueue() throws Exception {
        UUID rowId = enqueue(ENVIRONMENT_ID);

        relay.publishPending();

        Message received = rabbitTemplate.receive(QUEUE, 5000);
        assertThat(received).isNotNull();

        // El sobre transporta el identificador canonico. El header del broker es
        // solo el id de la fila del outbox y queda como respaldo.
        assertThat(received.getMessageProperties().getMessageId()).isEqualTo(rowId.toString());
        Object eventTypeHeader = received.getMessageProperties().getHeader("eventType");
        assertThat(eventTypeHeader).isEqualTo(EnvironmentalDataRecorded.TYPE);

        JsonNode envelope = jsonMapper.readTree(received.getBody());
        assertThat(envelope.get("eventType").asText())
                .isEqualTo(EnvironmentalDataRecorded.TYPE);
        assertThat(envelope.get("aggregateType").asText())
                .isEqualTo(EnvironmentalDataRecorded.AGGREGATE_TYPE);
        assertThat(envelope.get("version").asInt())
                .isEqualTo(EnvironmentalDataRecorded.SCHEMA_VERSION);
        assertThat(envelope.get("payload").get("environmentId").asText())
                .isEqualTo(ENVIRONMENT_ID.toString());
        assertThat(envelope.get("payload").get("temperature").asText()).isEqualTo("21.5");
        assertThat(envelope.get("payload").get("humidity").asText()).isEqualTo("55.0");
        assertThat(envelope.get("payload").get("co2").asText()).isEqualTo("612");
        assertThat(envelope.get("payload").get("noiseLevel").asText()).isEqualTo("48.0");
        assertThat(envelope.has("readings")).isFalse();
    }

    @Test
    void marksEventAsPublished() {
        UUID eventId = enqueue(ENVIRONMENT_ID);

        relay.publishPending();

        OutboxEvent event = repository.findById(eventId).orElseThrow();
        assertThat(event.isPending()).isFalse();
        assertThat(event.getPublishedAt()).isNotNull();
        assertThat(event.getAttempts()).isEqualTo(1);
        assertThat(event.getLastError()).isNull();
    }

    @Test
    void doesNotPublishTheSameEventTwice() {
        enqueue(ENVIRONMENT_ID);

        relay.publishPending();
        relay.publishPending();

        assertThat(rabbitTemplate.receive(QUEUE, 5000)).isNotNull();
        assertThat(rabbitTemplate.receive(QUEUE, 500)).isNull();
        assertThat(repository.countByPublishedAtIsNull()).isZero();
    }

    @Test
    void drainsTheWholeBatchInOnePass() {
        enqueue(UUID.fromString("33333333-3333-4333-8333-333333333333"));
        enqueue(UUID.fromString("44444444-4444-4444-8444-444444444444"));
        enqueue(UUID.fromString("55555555-5555-4555-8555-555555555555"));

        relay.publishPending();

        assertThat(repository.countByPublishedAtIsNull()).isZero();
        assertThat(rabbitTemplate.receive(QUEUE, 5000)).isNotNull();
        assertThat(rabbitTemplate.receive(QUEUE, 5000)).isNotNull();
        assertThat(rabbitTemplate.receive(QUEUE, 5000)).isNotNull();
    }

    @Test
    void keepsTrackOfAggregateForTracing() {
        enqueue(ENVIRONMENT_ID);

        List<OutboxEvent> byAggregate =
                repository.findByAggregateTypeAndAggregateIdOrderByOccurredAtAsc(
                        EnvironmentalDataRecorded.AGGREGATE_TYPE, ENVIRONMENT_ID.toString());

        assertThat(byAggregate).hasSize(1);
    }
}
