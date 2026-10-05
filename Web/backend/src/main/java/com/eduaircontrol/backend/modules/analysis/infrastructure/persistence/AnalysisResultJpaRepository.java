package com.eduaircontrol.backend.modules.analysis.infrastructure.persistence;

import com.eduaircontrol.backend.modules.analysis.domain.model.AnalysisResult;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface AnalysisResultJpaRepository extends JpaRepository<AnalysisResult, UUID> {

    @Query(value = """
            SELECT r.*
            FROM monitoring.analysis_result r
            WHERE r.environmental_analysis_id = :analysisId
            ORDER BY r.variable_id
            """, nativeQuery = true)
    List<AnalysisResult> findByEnvironmentalAnalysisId(@Param("analysisId") UUID analysisId);
}