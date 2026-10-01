package com.eduaircontrol.backend.modules.classrooms.application.port;

import com.eduaircontrol.backend.modules.classrooms.application.page.PageResult;
import com.eduaircontrol.backend.modules.classrooms.domain.model.EducationalEnvironment;
import com.eduaircontrol.backend.modules.classrooms.domain.model.RecordStatus;
import java.util.Optional;
import java.util.UUID;

public interface EducationalEnvironmentRepository {

    EducationalEnvironment save(EducationalEnvironment environment);

    Optional<EducationalEnvironment> findById(UUID id);

    boolean existsByCampusAndCode(UUID campusId, String code);

    boolean existsActiveCampus(UUID campusId);

    boolean existsActiveEnvironmentType(UUID environmentTypeId);

    PageResult<EducationalEnvironment> search(String query, RecordStatus status,
            UUID campusId, UUID environmentTypeId, int page, int limit);
}
