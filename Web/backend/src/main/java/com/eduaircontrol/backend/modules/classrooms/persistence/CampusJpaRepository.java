package com.eduaircontrol.backend.modules.classrooms.persistence;

import com.eduaircontrol.backend.modules.classrooms.domain.model.Campus;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CampusJpaRepository extends JpaRepository<Campus, UUID>, JpaSpecificationExecutor<Campus> {

    boolean existsByIdAndDeletedAtIsNull(UUID id);
}
