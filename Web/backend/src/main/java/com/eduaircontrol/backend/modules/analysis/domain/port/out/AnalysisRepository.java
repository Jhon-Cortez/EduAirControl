package com.eduaircontrol.backend.modules.analysis.domain.port.out;

import com.eduaircontrol.backend.modules.analysis.domain.model.AnalysisPeriod;
import com.eduaircontrol.backend.modules.analysis.domain.model.AnalysisStatusCode;
import com.eduaircontrol.backend.modules.analysis.domain.model.EnvironmentalAnalysis;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Salida de datos del modulo de analisis. El agregado se persiste completo con sus
 * resultados en cascada.
 */
public interface AnalysisRepository {

    EnvironmentalAnalysis save(EnvironmentalAnalysis analysis);

    Optional<EnvironmentalAnalysis> findById(UUID id);

    /** Ultimo analisis completado de un ambiente para una ventana concreta. */
    Optional<EnvironmentalAnalysis> findLatestCompleted(UUID environmentId,
                                                       AnalysisPeriod.Window window);

    boolean existsByEnvironmentAndWindow(UUID environmentId, AnalysisPeriod.Window window);

    Page<EnvironmentalAnalysis> search(UUID environmentId,
                                      AnalysisStatusCode status,
                                      Pageable pageable);

    /** Analisis que quedaron en RUNNING y deben reintentarse o marcarse como FAILED. */
    List<EnvironmentalAnalysis> findByStatus(AnalysisStatusCode status);
}