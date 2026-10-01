package com.eduaircontrol.backend.shared.contract;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato del catalogo de variables ambientales (modulo sensors).
 */
public interface VariableCatalogPort {

    Optional<VariableRef> findByCode(String code);

    List<VariableRef> findAll();

    Optional<VariableRef> findById(UUID variableId);

    record VariableRef(UUID id, String code, String name) {
    }
}
