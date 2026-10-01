package com.eduaircontrol.backend.modules.classrooms.application.port;

import com.eduaircontrol.backend.modules.classrooms.application.page.PageResult;
import com.eduaircontrol.backend.modules.classrooms.domain.model.Campus;
import com.eduaircontrol.backend.modules.classrooms.domain.model.RecordStatus;
import java.util.Optional;
import java.util.UUID;

public interface CampusRepository {

    Campus save(Campus campus);

    Optional<Campus> findById(UUID id);

    boolean existsByCode(String code);

    Optional<Campus> findByNameIgnoreCase(String name);

    PageResult<Campus> search(String query, RecordStatus status, int page, int limit);
}
