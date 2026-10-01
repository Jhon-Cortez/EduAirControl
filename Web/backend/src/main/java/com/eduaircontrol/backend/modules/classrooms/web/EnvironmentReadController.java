package com.eduaircontrol.backend.modules.classrooms.web;

import com.eduaircontrol.backend.modules.classrooms.readmodel.EnvironmentFormRequest;
import com.eduaircontrol.backend.modules.classrooms.readmodel.EnvironmentReadService;
import com.eduaircontrol.backend.modules.classrooms.readmodel.EnvironmentView;
import com.eduaircontrol.backend.modules.classrooms.readmodel.EnvironmentWriteService;
import com.eduaircontrol.backend.shared.contract.FavoritesPort;
import com.eduaircontrol.backend.shared.contract.UserIdentityPort;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Read model enriquecido de ambientes: lo consume el frontend (dashboard,
 * grid, detalle y gestion). El CRUD canonico de ms-classroom-management sigue
 * disponible en /api/v1/educational-environments.
 */
@RestController
@RequestMapping("/api/v1/environments")
@RequiredArgsConstructor
public class EnvironmentReadController {

    private final EnvironmentReadService readService;
    private final EnvironmentWriteService writeService;
    private final FavoritesPort favoritesPort;
    private final UserIdentityPort userIdentityPort;

    @GetMapping
    public List<EnvironmentView> list(@RequestParam(name = "q", required = false) String query,
                                      Authentication authentication) {
        return readService.list(email(authentication), query);
    }

    @GetMapping("/{id}")
    public EnvironmentView get(@PathVariable UUID id, Authentication authentication) {
        return readService.get(email(authentication), id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EnvironmentView> create(@Valid @RequestBody EnvironmentFormRequest request,
                                                  Authentication authentication) {
        EnvironmentView view = writeService.create(request, email(authentication));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(view.getId())
                .toUri();
        return ResponseEntity.created(location).body(view);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public EnvironmentView update(@PathVariable UUID id,
                                  @Valid @RequestBody EnvironmentFormRequest request,
                                  Authentication authentication) {
        return writeService.update(id, request, email(authentication));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable UUID id) {
        writeService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Ambiente eliminado"));
    }

    @PostMapping("/{id}/favorite")
    public Map<String, Object> toggleFavorite(@PathVariable UUID id, Authentication authentication) {
        UUID userId = userIdentityPort.idByEmail(email(authentication))
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.UNAUTHORIZED, "Sesión inválida"));
        boolean favorite = !favoritesPort.isFavorite(userId, id);
        favoritesPort.setFavorite(userId, id, favorite);
        return Map.of("id", id, "isFavorite", favorite);
    }

    private String email(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Sesión requerida");
        }
        return authentication.getName();
    }
}
