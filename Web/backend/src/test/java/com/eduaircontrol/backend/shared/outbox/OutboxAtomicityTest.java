package com.eduaircontrol.backend.shared.outbox;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Verifica la garantia que justifica el patron: el evento se escribe en la misma
 * transaccion que el cambio de negocio.
 *
 * <p>Se usa {@link TransactionTemplate} en vez de un servicio de prueba porque lo
 * que importa comprobar es la semantica de la transaccion compartida, no una
 * clase concreta.
 */
class OutboxAtomicityTest extends OutboxTestBase {

    @Autowired
    private TransactionTemplate transactionTemplate;

    private UUID appendCanonicalEvent() {
        EnvironmentalDataRecorded body = EnvironmentalDataRecorded.at(
                UUID.randomUUID(),
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
    void eventSurvivesWhenTheBusinessTransactionCommits() {
        UUID eventId = transactionTemplate.execute(status ->
                appendCanonicalEvent());

        assertThat(repository.findById(eventId)).isPresent();
    }

    @Test
    void eventDisappearsWhenTheBusinessTransactionRollsBack() {
        transactionTemplate.execute(status -> {
            appendCanonicalEvent();
            // Simula el fallo posterior del negocio: la exception revierte la
            // escritura del outbox junto con el resto del trabajo.
            status.setRollbackOnly();
            return null;
        });

        assertThat(repository.count()).isZero();
    }

    @Test
    void rolledBackEventIsNeverRelayed() {
        transactionTemplate.execute(status -> {
            appendCanonicalEvent();
            status.setRollbackOnly();
            return null;
        });

        relay.publishPending();

        assertThat(rabbitTemplate.receive(QUEUE, 1000)).isNull();
    }

    @Test
    void registersEveryAppendAsPending() {
        appendCanonicalEvent();

        assertThat(repository.countByPublishedAtIsNull()).isEqualTo(1);
    }
}
