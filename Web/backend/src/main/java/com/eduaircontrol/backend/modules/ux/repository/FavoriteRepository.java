package com.eduaircontrol.backend.modules.ux.repository;

import com.eduaircontrol.backend.modules.ux.entity.Favorite;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {
    List<Favorite> findByUserId(UUID userId);

    Optional<Favorite> findByUserIdAndClassroomId(UUID userId, UUID classroomId);

    void deleteByUserIdAndClassroomId(UUID userId, UUID classroomId);
}
