package com.eduaircontrol.backend.modules.sensors.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceCreateRequest {

    @NotBlank(message = "macAddress es obligatoria")
    @Pattern(regexp = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$",
            message = "macAddress debe tener formato AA:BB:CC:DD:EE:FF")
    private String macAddress;

    private String name;

    private String deviceType;

    private String firmwareVersion;

    private String ssid;

    private UUID educationalEnvironmentId;
}
