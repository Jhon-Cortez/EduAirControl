package com.eduaircontrol.backend.modules.device.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRequest {

    private static final String MAC_REGEX = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$";

    @NotBlank(message = "La direccion MAC es obligatoria")
    @Pattern(regexp = MAC_REGEX, message = "La direccion MAC no es valida (formato  AA:BB:CC:DD:EE:FF)")
    private String macAddress;

    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Pattern(regexp = "esp32|esp32s3|esp8266|otro",
             message = "Tipo de dispositivo no valido. Valores permitidos: esp32, esp32s3, esp8266, otro")
    private String tipo;

    @Min(value = 1, message = "El aula asignada debe ser valida")
    private Long idAula;

    @Size(max = 32, message = "El SSID no puede exceder 32 caracteres")
    private String ssid;

    @Pattern(regexp = "pendiente|conectado|offline|error",
             message = "Estado no valido. Valores permitidos: pendiente, conectado, offline, error")
    private String estado;

    @Size(max = 20, message = "La version de firmware no puede exceder 20 caracteres")
    private String firmwareVersion;
}
