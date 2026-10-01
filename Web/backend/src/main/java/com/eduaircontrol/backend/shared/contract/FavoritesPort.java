package com.eduaircontrol.backend.shared.contract;

import java.util.Set;
import java.util.UUID;

/**
 * Contrato de favoritos por usuario (modulo ux).
 */
public interface FavoritesPort {

    Set<UUID> favoriteIds(UUID userId);

    void setFavorite(UUID userId, UUID environmentId, boolean favorite);

    boolean isFavorite(UUID userId, UUID environmentId);
}
