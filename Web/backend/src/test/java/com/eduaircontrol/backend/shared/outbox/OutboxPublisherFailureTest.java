package com.eduaircontrol.backend.shared.outbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Verifica que un fallo del broker no pierde el evento.
 *
 * <p>Es la garantia critica del outbox: si publicar falla, el evento tiene que
 * seguir pendiente y reintentarse. Si la excepcion se propagara, la transaccion
 * de negocio (que comparte la tabla) ya habria hecho rollback y la medicion
 * guardada se perderia en cascada.
 */
class OutboxPublisherFailureTest extends OutboxTestBase {

    @MockitoBean
    private RabbitTemplate failingRabbitTemplate;

    @BeforeEach
    void brokerIsDown() {
        doThrow(new AmqpException("broker caido"))
                .when(failingRabbitTemplate)
                .convertAndSend(anyString(), anyString(), any(Object.class),
                        any(MessagePostProcessor.class));
    }

    private UUID enqueue() {
        EnvironmentalDataRecorded body = EnvironmentalDataRecorded.at(
                UUID.fromString("11111111-1111-4111-8111-111111111111"),
                Instant.now(),
                BigDecimal.ONE,
                BigDecimal.TEN,
                new BigDecimal("612"),
                new BigDecimal("48"),
                UUID.randomUUID());
        return writer.append(
                body.eventType(),
                body.aggregateType(),
                body.aggregateId().toString(),
                EnvironmentalDataRecorded.ROUTING_KEY,
                body,
                body.occurredAt());
    }

    @Test
    void keepsEventPendingAndCountsTheAttempt() {
        UUID eventId = enqueue();

        relay.publishPending();

        OutboxEvent event = repository.findById(eventId).orElseThrow();
        assertThat(event.isPending()).isTrue();
        assertThat(event.getAttempts()).isEqualTo(1);
        assertThat(event.getLastError()).contains("broker caido");
    }

    @Test
    void retriesOnEveryPassUntilItSucceeds() {
        UUID eventId = enqueue();

        relay.publishPending();
        relay.publishPending();
        relay.publishPending();

        OutboxEvent event = repository.findById(eventId).orElseThrow();
        assertThat(event.getAttempts()).isEqualTo(3);
        assertThat(event.isPending()).isTrue();
    }

    @Test
    void reportsStaleEventsThatHaveNeverBeenPublished() {
        enqueue();
        relay.publishPending();

        // El evento es reciente, asi que todavia no cuenta como deuda vieja.
        assertThat(relay.pendingOlderThanMinutes(5)).isZero();
        assertThat(relay.pendingOlderThanMinutes(0)).isEqualTo(1);
    }
}