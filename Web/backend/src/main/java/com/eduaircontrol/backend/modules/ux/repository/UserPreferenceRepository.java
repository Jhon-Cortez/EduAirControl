package com.eduaircontrol.backend.modules.ux.repository;

import com.eduaircontrol.backend.modules.ux.entity.UserPreference;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, UUID> {
    Optional<UserPreference> findByUserId(UUID userId);
}
