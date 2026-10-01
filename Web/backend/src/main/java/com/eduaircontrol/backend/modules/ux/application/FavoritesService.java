package com.eduaircontrol.backend.modules.ux.application;

import com.eduaircontrol.backend.modules.ux.entity.Favorite;
import com.eduaircontrol.backend.modules.ux.repository.FavoriteRepository;
import com.eduaircontrol.backend.shared.contract.FavoritesPort;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoritesService implements FavoritesPort {

    private final FavoriteRepository favoriteRepository;

    @Override
    @Transactional(readOnly = true)
    public Set<UUID> favoriteIds(UUID userId) {
        if (userId == null) {
            return Set.of();
        }
        return favoriteRepository.findByUserId(userId).stream()
                .map(Favorite::getClassroomId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void setFavorite(UUID userId, UUID environmentId, boolean favorite) {
        if (userId == null || environmentId == null) {
            return;
        }
        if (favorite) {
            favoriteRepository.findByUserIdAndClassroomId(userId, environmentId)
                    .orElseGet(() -> favoriteRepository.save(Favorite.builder()
                            .id(UUID.randomUUID())
                            .userId(userId)
                            .classroomId(environmentId)
                            .addedAt(Instant.now())
                            .build()));
            return;
        }
        favoriteRepository.deleteByUserIdAndClassroomId(userId, environmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(UUID userId, UUID environmentId) {
        return userId != null && environmentId != null
                && favoriteRepository.findByUserIdAndClassroomId(userId, environmentId).isPresent();
    }
}
