package com.eduaircontrol.backend.modules.monitoring.repository;

import com.eduaircontrol.backend.modules.monitoring.entity.EnvironmentAlert;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnvironmentAlertRepository extends JpaRepository<EnvironmentAlert, UUID> {

    List<EnvironmentAlert> findTop50ByDeletedAtIsNullOrderByRaisedAtDesc();

    long countByResolvedAtIsNullAndDeletedAtIsNull();

    boolean existsByEducationalEnvironmentIdAndVariableIdAndResolvedAtIsNullAndDeletedAtIsNull(
            UUID educationalEnvironmentId, UUID variableId);

    @Query("""
            select a from EnvironmentAlert a
            where a.deletedAt is null
              and a.educationalEnvironmentId = :environmentId
              and a.resolvedAt is null
            order by a.raisedAt desc
            """)
    List<EnvironmentAlert> findOpenByEnvironment(@Param("environmentId") UUID environmentId);
}
