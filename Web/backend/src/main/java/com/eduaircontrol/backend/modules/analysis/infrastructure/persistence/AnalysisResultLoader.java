package com.eduaircontrol.backend.modules.analysis.infrastructure.persistence;

import com.eduaircontrol.backend.modules.analysis.domain.model.AnalysisResult;
import com.eduaircontrol.backend.modules.analysis.domain.model.EnvironmentalAnalysis;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Carga explicita de los resultados de un analisis. La relacion es lazy por diseno
 * para no penalizar las consultas de listado, pero toda lectura de la API incluye los
 * resultados, asi que se cargan aqui de forma explicita.
 */
@Component
@RequiredArgsConstructor
class AnalysisResultLoader {

    private final AnalysisResultJpaRepository results;

    @Transactional(readOnly = true)
    EnvironmentalAnalysis withResults(EnvironmentalAnalysis analysis) {
        List<AnalysisResult> loaded = results.findByEnvironmentalAnalysisId(analysis.getId());
        analysis.getResults().clear();
        loaded.forEach(result -> {
            result.setEnvironmentalAnalysis(analysis);
            analysis.getResults().add(result);
        });
        return analysis;
    }
}