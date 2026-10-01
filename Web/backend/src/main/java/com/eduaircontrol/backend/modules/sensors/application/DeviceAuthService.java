package com.eduaircontrol.backend.modules.sensors.application;

import com.eduaircontrol.backend.modules.sensors.entity.Device;
import com.eduaircontrol.backend.modules.sensors.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Autenticacion de los dispositivos ESP32 por X-API-Key: el prefijo localiza el
 * registro y el hash BCrypt verifica la clave completa.
 */
@Service
@RequiredArgsConstructor
public class DeviceAuthService {

    public static final int PREFIX_LENGTH = 12;

    private final DeviceRepository deviceRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Device authenticate(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "X-API-Key requerida");
        }
        String prefix = apiKey.substring(0, Math.min(PREFIX_LENGTH, apiKey.length()));
        Device device = deviceRepository.findByApiKeyPrefix(prefix)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "API key inválida"));
        if (device.getApiKeyHash() == null
                || !passwordEncoder.matches(apiKey, device.getApiKeyHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "API key inválida");
        }
        return device;
    }
}
