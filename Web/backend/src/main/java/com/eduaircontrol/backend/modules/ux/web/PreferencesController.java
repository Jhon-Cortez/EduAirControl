package com.eduaircontrol.backend.modules.ux.web;

import com.eduaircontrol.backend.modules.ux.application.PreferencesService;
import com.eduaircontrol.backend.modules.ux.dto.PreferencesRequest;
import com.eduaircontrol.backend.modules.ux.dto.PreferencesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/preferences")
@RequiredArgsConstructor
public class PreferencesController {

    private final PreferencesService preferencesService;

    @GetMapping
    public PreferencesResponse get(Authentication authentication) {
        return preferencesService.get(currentEmail(authentication));
    }

    @PutMapping
    public PreferencesResponse save(@RequestBody PreferencesRequest request,
                                    Authentication authentication) {
        return preferencesService.save(currentEmail(authentication), request);
    }

    private String currentEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Sesión requerida");
        }
        return authentication.getName();
    }
}
