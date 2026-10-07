package com.eduaircontrol.backend.shared.outbox;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declara el exchange de datos ambientales. Es <b>topic</b> y no direct: asi
 * ms-environment-monitoring puede suscribirse a {@code environmental.data.*} y,
 * mas adelante, otro servicio a {@code thresholds.*}, sin que el productor cambie.
 *
 * <p>Durable porque el broker sobrevive reinicios y los eventos pendientes en la
 * tabla outbox deben reencontrar su cola al volver.
 */
@Configuration
@ConditionalOnProperty(prefix = "app.outbox", name = "enabled", havingValue = "true",
        matchIfMissing = true)
public class OutboxMessagingConfig {

    @Bean
    public TopicExchange measurementsExchange(
            @Value("${app.outbox.exchange:eduaircontrol.environmental-data}") String name) {
        return new TopicExchange(name, true, false);
    }
}
