package com.eduaircontrol.backend.modules.analysis.infrastructure.persistence;

import com.eduaircontrol.backend.modules.analysis.domain.model.AnalysisPeriod;
import com.eduaircontrol.backend.modules.analysis.domain.model.AnalysisStatus;
import com.eduaircontrol.backend.modules.analysis.domain.model.AnalysisStatusCode;
import com.eduaircontrol.backend.modules.analysis.domain.model.EnvironmentalAnalysis;
import com.eduaircontrol.backend.modules.analysis.domain.port.out.AnalysisRepository;
import com.eduaircontrol.backend.modules.analysis.domain.port.out.AnalysisStatusRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adaptador JPA del agregado. Los resultados se cargan de forma explicita porque la
 * respuesta los necesita siempre y la ventana de analisis siempre los escribe.
 */
@Repository
@RequiredArgsConstructor
public class AnalysisRepositoryAdapter implements AnalysisRepository {

    private final EnvironmentalAnalysisJpaRepository analyses;
    private final AnalysisResultLoader resultLoader;

    @Override
    @Transactional
    public EnvironmentalAnalysis save(EnvironmentalAnalysis analysis) {
        return analyses.save(analysis);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EnvironmentalAnalysis> findById(UUID id) {
        return analyses.findById(id).map(resultLoader::withResults);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EnvironmentalAnalysis> findLatestCompleted(UUID environmentId,
                                                              AnalysisPeriod.Window window) {
        UUID latestId = analyses.findLatestIdByEnvironmentAndStatus(environmentId,
                AnalysisStatusCode.COMPLETED.name());
        if (latestId == null) {
            return Optional.empty();
        }
        return findById(latestId).filter(analysis -> analysis.sameWindow(window));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEnvironmentAndWindow(UUID environmentId, AnalysisPeriod.Window window) {
        return analyses.countByEnvironmentAndWindow(environmentId, window.start(), window.end()) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnvironmentalAnalysis> search(UUID environmentId,
                                             AnalysisStatusCode status,
                                             Pageable pageable) {
        Page<EnvironmentalAnalysis> page = analyses.search(environmentId,
                status == null ? null : status.name(),
                pageable);
        page.getContent().forEach(resultLoader::withResults);
        return page;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvironmentalAnalysis> findByStatus(AnalysisStatusCode status) {
        return analyses.findByStatusCode(status.name()).stream()
                .map(resultLoader::withResults)
                .toList();
    }

    @Repository
    @RequiredArgsConstructor
    static class AnalysisStatusRepositoryAdapter implements AnalysisStatusRepository {

        private final AnalysisStatusJpaRepository statuses;

        @Override
        @Transactional(readOnly = true)
        public AnalysisStatus get(AnalysisStatusCode code) {
            return statuses.findByCode(code.name())
                    .orElseThrow(() -> new IllegalStateException(
                            "analysis status not seeded: " + code.name()));
        }

        @Override
        @Transactional(readOnly = true)
        public List<AnalysisStatus> findAll() {
            return statuses.findAllByOrderByCodeAsc();
        }
    }
}