package com.eduaircontrol.backend.shared.contract;

import java.util.Optional;
import java.util.UUID;

/**
 * Contrato de identidad minima para otros modulos: resolver el usuario actual
 * (el JWT transporta el correo) a su UUID canonico.
 */
public interface UserIdentityPort {

    Optional<UUID> idByEmail(String email);
}
