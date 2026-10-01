package com.eduaircontrol.backend.modules.classrooms.application.port;

import com.eduaircontrol.backend.modules.classrooms.application.page.PageResult;
import com.eduaircontrol.backend.modules.classrooms.domain.model.EnvironmentType;
import java.util.Optional;
import java.util.UUID;

public interface EnvironmentTypeRepository {

    EnvironmentType save(EnvironmentType environmentType);

    Optional<EnvironmentType> findById(UUID id);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    Optional<EnvironmentType> findByNameIgnoreCase(String name);

    PageResult<EnvironmentType> search(String query, int page, int limit);
}
