package com.eduaircontrol.backend.modules.identity.web;

import com.eduaircontrol.backend.modules.identity.dto.request.ProfileUpdateRequest;
import com.eduaircontrol.backend.modules.identity.dto.response.ProfileResponse;
import com.eduaircontrol.backend.modules.identity.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ProfileResponse get(Authentication authentication) {
        return profileService.get(currentEmail(authentication));
    }

    @PutMapping
    public ProfileResponse update(@Valid @RequestBody ProfileUpdateRequest request,
                                  Authentication authentication) {
        return profileService.update(currentEmail(authentication), request);
    }

    private String currentEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Sesión requerida");
        }
        return authentication.getName();
    }
}
