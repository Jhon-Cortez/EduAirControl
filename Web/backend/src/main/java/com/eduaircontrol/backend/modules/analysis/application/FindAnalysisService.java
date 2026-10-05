package com.eduaircontrol.backend.modules.analysis.application;

import com.eduaircontrol.backend.modules.analysis.domain.model.AnalysisPeriod;
import com.eduaircontrol.backend.modules.analysis.domain.model.EnvironmentalAnalysis;
import com.eduaircontrol.backend.modules.analysis.domain.port.in.FindAnalysisUseCase;
import com.eduaircontrol.backend.modules.analysis.domain.port.out.AnalysisRepository;
import com.eduaircontrol.backend.modules.analysis.domain.service.PeriodResolver;
import com.eduaircontrol.backend.modules.classrooms.domain.exception.NotFoundException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FindAnalysisService implements FindAnalysisUseCase {

    private final AnalysisRepository analysisRepository;
    private final PeriodResolver periodResolver;

    @Override
    @Transactional(readOnly = true)
    public Optional<EnvironmentalAnalysis> byId(UUID id) {
        return analysisRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnvironmentalAnalysis> search(Query query) {
        return analysisRepository.search(query.environmentId(), query.status(), query.pageable());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EnvironmentalAnalysis> latestCompleted(UUID environmentId,
                                                           AnalysisPeriod period,
                                                           Instant referenceDate) {
        AnalysisPeriod.Window window = periodResolver.resolve(period, referenceDate);
        return analysisRepository.findLatestCompleted(environmentId, window);
    }

    public EnvironmentalAnalysis require(UUID id) {
        return byId(id).orElseThrow(() -> new NotFoundException("Analisis no encontrado: " + id));
    }
}