package com.eduaircontrol.backend.modules.sensors.dto;

import com.eduaircontrol.backend.modules.sensors.entity.Device;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeviceResponse {

    private final UUID id;
    private final String macAddress;
    private final String name;
    private final String deviceType;
    private final String firmwareVersion;
    private final String ssid;
    private final String status;
    private final UUID educationalEnvironmentId;
    private final Instant lastSeenAt;
    /** Solo presente en el alta o rotacion de clave. */
    private final String apiKey;
    /** Solo presente en el alta o rotacion de clave. */
    private final String keyPrefix;

    public static DeviceResponse from(Device device) {
        return DeviceResponse.builder()
                .id(device.getId())
                .macAddress(device.getMacAddress())
                .name(device.getName())
                .deviceType(device.getDeviceType())
                .firmwareVersion(device.getFirmwareVersion())
                .ssid(device.getSsid())
                .status(device.getStatus())
                .educationalEnvironmentId(device.getEducationalEnvironmentId())
                .lastSeenAt(device.getLastSeenAt())
                .keyPrefix(device.getApiKeyPrefix())
                .build();
    }

    public static DeviceResponse withKey(Device device, String apiKey) {
        return DeviceResponse.builder()
                .id(device.getId())
                .macAddress(device.getMacAddress())
                .name(device.getName())
                .deviceType(device.getDeviceType())
                .firmwareVersion(device.getFirmwareVersion())
                .ssid(device.getSsid())
                .status(device.getStatus())
                .educationalEnvironmentId(device.getEducationalEnvironmentId())
                .lastSeenAt(device.getLastSeenAt())
                .apiKey(apiKey)
                .keyPrefix(device.getApiKeyPrefix())
                .build();
    }
}
