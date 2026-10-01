package com.eduaircontrol.backend.modules.ux.web;

import com.eduaircontrol.backend.modules.ux.application.FavoritesService;
import com.eduaircontrol.backend.shared.contract.UserIdentityPort;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoritesController {

    private final FavoritesService favoritesService;
    private final UserIdentityPort userIdentityPort;

    @GetMapping
    public List<UUID> list(Authentication authentication) {
        UUID userId = userId(currentEmail(authentication));
        return favoritesService.favoriteIds(userId).stream().sorted().toList();
    }

    @PostMapping("/{environmentId}/toggle")
    public Map<String, Object> toggle(@PathVariable UUID environmentId, Authentication authentication) {
        UUID userId = userId(currentEmail(authentication));
        boolean favorite = !favoritesService.isFavorite(userId, environmentId);
        favoritesService.setFavorite(userId, environmentId, favorite);
        return Map.of("environmentId", environmentId, "favorite", favorite);
    }

    @DeleteMapping("/{environmentId}")
    public ResponseEntity<Map<String, String>> remove(@PathVariable UUID environmentId,
                                                      Authentication authentication) {
        favoritesService.setFavorite(userId(currentEmail(authentication)), environmentId, false);
        return ResponseEntity.ok(Map.of("message", "Favorito eliminado"));
    }

    private UUID userId(String email) {
        return userIdentityPort.idByEmail(email)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.UNAUTHORIZED, "Sesión inválida"));
    }

    private String currentEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Sesión requerida");
        }
        return authentication.getName();
    }
}
