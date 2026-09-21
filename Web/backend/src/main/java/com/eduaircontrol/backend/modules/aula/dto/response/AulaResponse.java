package com.eduaircontrol.backend.modules.aula.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AulaResponse {
    private Long id;
    private String codigoAula;
    private String nombre;
    private String ubicacion;
    private Integer capacidad;
    private String tipoAula;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
