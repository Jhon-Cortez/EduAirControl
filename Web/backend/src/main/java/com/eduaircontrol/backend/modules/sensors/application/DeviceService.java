package com.eduaircontrol.backend.modules.sensors.application;

import com.eduaircontrol.backend.modules.sensors.dto.DeviceCreateRequest;
import com.eduaircontrol.backend.modules.sensors.dto.DeviceResponse;
import com.eduaircontrol.backend.modules.sensors.dto.DeviceUpdateRequest;
import com.eduaircontrol.backend.modules.sensors.entity.Device;
import com.eduaircontrol.backend.modules.sensors.repository.DeviceRepository;
import com.eduaircontrol.backend.shared.contract.EnvironmentLookupPort;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final DeviceRepository deviceRepository;
    private final EnvironmentLookupPort environmentLookupPort;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<DeviceResponse> list() {
        return deviceRepository.findAll().stream().map(DeviceResponse::from).toList();
    }

    @Transactional
    public DeviceResponse create(DeviceCreateRequest request) {
        deviceRepository.findByMacAddress(request.getMacAddress().toUpperCase())
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Ya existe un dispositivo con esa MAC");
                });
        if (request.getEducationalEnvironmentId() != null) {
            environmentLookupPort.findById(request.getEducationalEnvironmentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Ambiente no encontrado"));
        }
        Device device = Device.builder()
                .id(UUID.randomUUID())
                .macAddress(request.getMacAddress().toUpperCase())
                .name(request.getName())
                .deviceType(defaultText(request.getDeviceType(), "esp32"))
                .firmwareVersion(request.getFirmwareVersion())
                .ssid(request.getSsid())
                .status("pendiente")
                .educationalEnvironmentId(request.getEducationalEnvironmentId())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        String apiKey = generateApiKey();
        applyKey(device, apiKey);
        return DeviceResponse.withKey(deviceRepository.save(device), apiKey);
    }

    @Transactional
    public DeviceResponse rotateKey(String macAddress) {
        Device device = deviceRepository.findByMacAddress(macAddress.toUpperCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Dispositivo no encontrado"));
        String apiKey = generateApiKey();
        applyKey(device, apiKey);
        device.setUpdatedAt(Instant.now());
        return DeviceResponse.withKey(deviceRepository.save(device), apiKey);
    }

    @Transactional
    public DeviceResponse update(UUID id, DeviceUpdateRequest request) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Dispositivo no encontrado"));
        if (request.getEducationalEnvironmentId() != null) {
            environmentLookupPort.findById(request.getEducationalEnvironmentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Ambiente no encontrado"));
        }
        if (request.getName() != null) {
            device.setName(request.getName());
        }
        if (request.getDeviceType() != null && !request.getDeviceType().isBlank()) {
            device.setDeviceType(request.getDeviceType());
        }
        if (request.getFirmwareVersion() != null) {
            device.setFirmwareVersion(request.getFirmwareVersion());
        }
        if (request.getSsid() != null) {
            device.setSsid(request.getSsid());
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            device.setStatus(request.getStatus());
        }
        if (request.getEducationalEnvironmentId() != null) {
            device.setEducationalEnvironmentId(request.getEducationalEnvironmentId());
        }
        device.setUpdatedAt(Instant.now());
        return DeviceResponse.from(deviceRepository.save(device));
    }

    @Transactional
    public void delete(UUID id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Dispositivo no encontrado"));
        deviceRepository.delete(device);
    }

    private void applyKey(Device device, String apiKey) {
        device.setApiKeyPrefix(apiKey.substring(0, DeviceAuthService.PREFIX_LENGTH));
        device.setApiKeyHash(passwordEncoder.encode(apiKey));
    }

    /**
     * Clave opaca: no se vuelve a mostrar tras el alta/rotacion; solo se guarda
     * su hash BCrypt y un prefijo para localizar el registro.
     */
    private String generateApiKey() {
        byte[] bytes = new byte[24];
        RANDOM.nextBytes(bytes);
        return "ea_" + HexFormat.of().formatHex(bytes);
    }

    private String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
