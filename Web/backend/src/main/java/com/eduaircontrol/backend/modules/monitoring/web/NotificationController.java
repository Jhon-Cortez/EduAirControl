package com.eduaircontrol.backend.modules.monitoring.web;

import com.eduaircontrol.backend.modules.monitoring.application.NotificationService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationService.NotificationResponse> list(Authentication authentication) {
        return notificationService.list(currentEmail(authentication));
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(Authentication authentication) {
        return Map.of("count", notificationService.unreadCount(currentEmail(authentication)));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markRead(@PathVariable UUID id,
                                                        Authentication authentication) {
        notificationService.markRead(currentEmail(authentication), id);
        return ResponseEntity.ok(Map.of("message", "Notificación leída"));
    }

    @PostMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllRead(Authentication authentication) {
        notificationService.markAllRead(currentEmail(authentication));
        return ResponseEntity.ok(Map.of("message", "Notificaciones leídas"));
    }

    private String currentEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Sesión requerida");
        }
        return authentication.getName();
    }
}
