package com.eduaircontrol.backend.modules.analysis.infrastructure.persistence;

import com.eduaircontrol.backend.modules.analysis.domain.model.AnalysisStatusCode;
import com.eduaircontrol.backend.modules.analysis.domain.model.EnvironmentalAnalysis;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface EnvironmentalAnalysisJpaRepository
        extends JpaRepository<EnvironmentalAnalysis, UUID> {

    @Query(value = """
            SELECT a.environmental_analysis_id
            FROM monitoring.environmental_analysis a
            JOIN monitoring.analysis_statuses s ON s.analysis_status_id = a.analysis_status_id
            WHERE a.educational_environment_id = :environmentId
              AND s.code = :status
            ORDER BY a.period_start DESC, a.computed_at DESC NULLS LAST
            LIMIT 1
            """, nativeQuery = true)
    UUID findLatestIdByEnvironmentAndStatus(@Param("environmentId") UUID environmentId,
                                            @Param("status") String status);

    @Query(value = """
            SELECT COUNT(*)
            FROM monitoring.environmental_analysis a
            WHERE a.educational_environment_id = :environmentId
              AND a.period_start = :periodStart
              AND a.period_end = :periodEnd
            """, nativeQuery = true)
    long countByEnvironmentAndWindow(@Param("environmentId") UUID environmentId,
                                     @Param("periodStart") java.time.Instant periodStart,
                                     @Param("periodEnd") java.time.Instant periodEnd);

    @Query("""
            SELECT a FROM EnvironmentalAnalysis a
            JOIN FETCH a.status s
            WHERE (:environmentId IS NULL OR a.educationalEnvironmentId = :environmentId)
              AND (:status IS NULL OR s.code = :status)
            """)
    org.springframework.data.domain.Page<EnvironmentalAnalysis> search(
            @Param("environmentId") UUID environmentId,
            @Param("status") String status,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT a FROM EnvironmentalAnalysis a WHERE a.status.code = :status")
    List<EnvironmentalAnalysis> findByStatusCode(@Param("status") String status);
}