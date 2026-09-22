package com.eduaircontrol.backend.modules.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Correo inválido")
    private String email;
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    @NotBlank(message = "El código de empresa es obligatorio")
    @Pattern(regexp = "^[A-Z]{3}-\\d{4}$", message = "Formato de código de empresa inválido")
    private String companyCode;
}
