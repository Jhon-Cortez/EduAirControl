package com.eduaircontrol.backend.modules.sensors.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngestRequest {

    /** Serial del sensor (EA-<ambiente>-<T|H|C>). Debe pertenecer al ambiente del dispositivo. */
    private String serial;

    @NotEmpty(message = "readings no puede estar vacío")
    private List<Reading> readings;

    /** Momento por defecto de las lecturas; cada reading puede sobreescribirlo. */
    private Instant measuredAt;

    @Getter
    @Setter
    public static class Reading {

        @NotBlank(message = "variable es obligatoria")
        private String variable;

        @NotNull(message = "value es obligatorio")
        private BigDecimal value;

        /** Si falta, se usa measuredAt del payload o la hora actual. */
        private Instant measuredAt;
    }
}
