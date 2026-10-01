package com.eduaircontrol.backend.modules.sensors.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SensorSaveRequest {

    @NotNull(message = "environmentId is required")
    private UUID environmentId;

    @NotNull(message = "variable is required")
    private String variable;

    /** Rango operativo mostrado en la UI (se persiste como umbral WARNING). */
    private BigDecimal min;

    private BigDecimal max;

    /** "active" | "offline" | "warning" (solo afecta a la instalacion). */
    private String status;

    /** Numero de serie opcional; si falta se genera del ambiente y la variable. */
    private String id;
}
