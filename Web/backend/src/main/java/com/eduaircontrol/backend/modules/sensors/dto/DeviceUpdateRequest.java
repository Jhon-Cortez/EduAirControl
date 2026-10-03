package com.eduaircontrol.backend.modules.sensors.dto;

import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Edicion de un dispositivo existente (la MAC identifica y no se modifica).
 * Los nulos conservan el valor actual.
 */
@Getter
@Setter
public class DeviceUpdateRequest {

    @Size(max = 100)
    private String name;

    @Size(max = 50)
    private String deviceType;

    @Size(max = 20)
    private String firmwareVersion;

    @Size(max = 32)
    private String ssid;

    @Size(max = 20)
    private String status;

    private UUID educationalEnvironmentId;
}
