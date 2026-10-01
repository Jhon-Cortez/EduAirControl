package com.eduaircontrol.backend.modules.ux.application;

import com.eduaircontrol.backend.modules.ux.dto.PreferencesRequest;
import com.eduaircontrol.backend.modules.ux.dto.PreferencesResponse;
import com.eduaircontrol.backend.modules.ux.entity.UserPreference;
import com.eduaircontrol.backend.modules.ux.repository.UserPreferenceRepository;
import com.eduaircontrol.backend.shared.contract.UserIdentityPort;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PreferencesService {

    private final UserPreferenceRepository preferenceRepository;
    private final UserIdentityPort userIdentityPort;

    @Transactional(readOnly = true)
    public PreferencesResponse get(String email) {
        return PreferencesResponse.from(requirePreference(email));
    }

    @Transactional
    public PreferencesResponse save(String email, PreferencesRequest request) {
        UserPreference preference = requirePreference(email);
        if (request.getLanguage() != null) {
            preference.setLanguage(request.getLanguage());
        }
        if (request.getDateFormat() != null) {
            preference.setDateFormat(request.getDateFormat());
        }
        if (request.getManualTimezone() != null) {
            preference.setTimeZone(request.getManualTimezone());
        }
        if (request.getReminders() != null) {
            preference.setReminders(request.getReminders());
        }
        if (request.getColorTheme() != null) {
            preference.setColorTheme(request.getColorTheme());
        }
        if (request.getDarkMode() != null) {
            preference.setDarkMode(request.getDarkMode());
        }
        if (request.getAutoTimezone() != null) {
            preference.setAutoTimeZone(request.getAutoTimezone());
        }
        if (request.getNotificationsEnabled() != null) {
            preference.setNotificationsEnabled(request.getNotificationsEnabled());
        }
        return PreferencesResponse.from(preferenceRepository.save(preference));
    }

    private UserPreference requirePreference(String email) {
        UUID userId = userIdentityPort.idByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión inválida"));
        return preferenceRepository.findByUserId(userId).orElseGet(() -> {
            UserPreference preference = new UserPreference();
            preference.setId(UUID.randomUUID());
            preference.setUserId(userId);
            preference.setLanguage("es");
            preference.setDateFormat("DD-MM-YYYY");
            preference.setNotificationsEnabled(true);
            preference.setAutoTimeZone(true);
            preference.setDarkMode(false);
            preference.setColorTheme("");
            preference.setTimeZone("America/Lima");
            preference.setReminders(new java.util.LinkedHashMap<>(Map.of(
                    "alertas", true,
                    "advertencias", true,
                    "resumenDiario", false,
                    "sonido", true)));
            return preferenceRepository.save(preference);
        });
    }
}
