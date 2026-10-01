package com.eduaircontrol.backend.modules.monitoring.repository;

import com.eduaircontrol.backend.modules.monitoring.entity.VariableThreshold;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VariableThresholdRepository extends JpaRepository<VariableThreshold, UUID> {

    @Query("""
            select t from VariableThreshold t
            where t.validTo is null
              and t.variableId = :variableId
              and (t.educationalEnvironmentId = :environmentId
                   or (t.educationalEnvironmentId is null and t.environmentTypeId = :environmentTypeId))
            order by case when t.educationalEnvironmentId is not null then 0 else 1 end
            """)
    List<VariableThreshold> findEffective(@Param("environmentId") UUID environmentId,
                                          @Param("environmentTypeId") UUID environmentTypeId,
                                          @Param("variableId") UUID variableId);

    @Query("""
            select t from VariableThreshold t
            where t.validTo is null
              and t.severityId = :severityId
              and (t.educationalEnvironmentId = :environmentId
                   or (t.educationalEnvironmentId is null and t.environmentTypeId = :environmentTypeId))
            """)
    List<VariableThreshold> findEffectiveBySeverity(@Param("environmentId") UUID environmentId,
                                                    @Param("environmentTypeId") UUID environmentTypeId,
                                                    @Param("severityId") UUID severityId);

    @Query("""
            select t from VariableThreshold t
            where t.validTo is null
              and t.severityId = :severityId
              and (t.educationalEnvironmentId in :environmentIds
                   or (t.educationalEnvironmentId is null and t.environmentTypeId in :environmentTypeIds))
            """)
    List<VariableThreshold> findEffectiveBySeverity(@Param("environmentIds") java.util.Collection<UUID> environmentIds,
                                                    @Param("environmentTypeIds") java.util.Collection<UUID> environmentTypeIds,
                                                    @Param("severityId") UUID severityId);

    Optional<VariableThreshold> findByEducationalEnvironmentIdAndVariableIdAndValidToIsNull(
            UUID educationalEnvironmentId, UUID variableId);

    @Modifying
    @Query("delete from VariableThreshold t where t.educationalEnvironmentId = :environmentId")
    void deleteByEducationalEnvironment(@Param("environmentId") UUID environmentId);
}
