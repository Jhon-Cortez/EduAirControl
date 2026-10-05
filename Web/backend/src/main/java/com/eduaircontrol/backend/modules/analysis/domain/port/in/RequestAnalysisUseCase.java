package com.eduaircontrol.backend.modules.analysis.domain.port.in;

import com.eduaircontrol.backend.modules.analysis.domain.model.AnalysisPeriod;
import com.eduaircontrol.backend.modules.analysis.domain.model.EnvironmentalAnalysis;
import java.time.Instant;
import java.util.UUID;

/**
 * Caso de uso de entrada: solicita y calcula el analisis de un ambiente para un
 * periodo. El calculo es sincrono, por lo que devuelve el analisis ya resuelto.
 */
public interface RequestAnalysisUseCase {

    EnvironmentalAnalysis execute(Command command);

    /**
     * @param environmentId ambiente educativo analizado
     * @param period        granularidad del periodo
     * @param referenceDate ancla opcional del periodo; si se omite se usa el instante actual
     * @param requestedBy   usuario que solicita el analisis
     */
    record Command(UUID environmentId,
                   AnalysisPeriod period,
                   Instant referenceDate,
                   UUID requestedBy) {
    }
}