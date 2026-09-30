package com.eduaircontrol.backend.modules.device.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceResponse {
    private Long id;
    private String macAddress;
    private String nombre;
    private String tipo;
    private Long idAula;
    private String ssid;
    private String estado;
    private String firmwareVersion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
