package com.eduaircontrol.backend.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "El correo es obligatorio")
    private String email;
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    @NotBlank(message = "El código de empresa es obligatorio")
    @Pattern(regexp = "^[A-Z]{3}-\\d{4}$", message = "Formato de código de empresa inválido")
    private String companyCode;
}
