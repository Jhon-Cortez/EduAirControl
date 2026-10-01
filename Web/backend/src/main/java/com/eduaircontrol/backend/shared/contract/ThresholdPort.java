package com.eduaircontrol.backend.shared.contract;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato de umbrales (modulo monitoring). Precedencia: umbral por ambiente
 * (educational_environment_id) sobre el umbral por tipo de ambiente.
 */
public interface ThresholdPort {

    Optional<Range> warningRange(UUID environmentId, UUID variableId);

    java.util.List<Range> warningRanges(UUID environmentId);

    void saveWarningRange(UUID environmentId, UUID environmentTypeId, UUID variableId,
                          BigDecimal min, BigDecimal max);

    void deleteByEnvironment(UUID environmentId);

    record Range(UUID environmentId, UUID variableId, BigDecimal min, BigDecimal max) {
    }
}
