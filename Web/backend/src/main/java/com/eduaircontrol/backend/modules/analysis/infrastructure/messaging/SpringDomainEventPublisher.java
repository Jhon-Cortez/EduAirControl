package com.eduaircontrol.backend.modules.analysis.infrastructure.messaging;

import com.eduaircontrol.backend.modules.analysis.domain.port.out.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Publica los eventos de dominio en el bus interno de Spring.
 *
 * <p>Cuando el servicio se extraiga del monolito, esta clase pasa a escribir en la
 * tabla outbox dentro de la misma transaccion y un publicador en segundo plano los
 * envia a RabbitMQ (ADR-007). Los casos de uso no cambian.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(Object event) {
        log.debug("Publishing domain event {}", event.getClass().getSimpleName());
        applicationEventPublisher.publishEvent(event);
    }
}