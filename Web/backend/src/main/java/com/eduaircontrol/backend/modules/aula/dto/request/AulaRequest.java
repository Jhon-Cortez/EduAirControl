package com.eduaircontrol.backend.modules.aula.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AulaRequest {

    @NotBlank(message = "El codigo del aula es obligatorio")
    @Size(max = 20, message = "El codigo no puede exceder 20 caracteres")
    private String codigoAula;

    @NotBlank(message = "El nombre del aula es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Size(max = 150, message = "La ubicacion no puede exceder 150 caracteres")
    private String ubicacion;

    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    @Max(value = 1000, message = "La capacidad no puede exceder 1000")
    private Integer capacidad;

    @NotBlank(message = "El tipo de aula es obligatorio")
    @Pattern(regexp = "salon|laboratorio|auditorio|sala_computo|biblioteca",
             message = "Tipo de aula no valido. Valores permitidos: salon, laboratorio, auditorio, sala_computo, biblioteca")
    private String tipoAula;

    @Pattern(regexp = "activa|inactiva|mantenimiento",
             message = "Estado no valido. Valores permitidos: activa, inactiva, mantenimiento")
    private String estado;
}
