package com.eduaircontrol.backend.modules.classrooms.persistence;

import com.eduaircontrol.backend.modules.classrooms.domain.model.EducationalEnvironment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EducationalEnvironmentJpaRepository
        extends JpaRepository<EducationalEnvironment, UUID>, JpaSpecificationExecutor<EducationalEnvironment> {

    boolean existsByCampusIdAndCode(UUID campusId, String code);
}
