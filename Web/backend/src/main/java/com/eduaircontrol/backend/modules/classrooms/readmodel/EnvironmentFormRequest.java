package com.eduaircontrol.backend.modules.classrooms.readmodel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Formulario del frontend (AddEnvironmentModal / EditEnvironmentModal).
 * floor llega como texto libre y tempMin/tempMax definen el umbral de temperatura.
 */
@Getter
@Setter
public class EnvironmentFormRequest {

    @NotBlank(message = "name is required")
    @Size(max = 120)
    private String name;

    @Size(max = 120)
    private String location;

    private String floor;

    private BigDecimal capacity;

    @Size(max = 80)
    private String envType;

    private BigDecimal tempMin;

    private BigDecimal tempMax;

    private UUID campusId;

    private UUID environmentTypeId;
}
