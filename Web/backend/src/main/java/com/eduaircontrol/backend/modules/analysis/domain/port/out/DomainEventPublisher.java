package com.eduaircontrol.backend.modules.analysis.domain.port.out;

/**
 * Publicacion de eventos de dominio. Enquanto el sistema sea monolithico se resuelve
 * con el bus de aplicacion; cuando el servicio se extraiga, la misma interfaz pasa a
 * publicar por outbox transaccional (ADR-007).
 */
public interface DomainEventPublisher {

    void publish(Object event);
}