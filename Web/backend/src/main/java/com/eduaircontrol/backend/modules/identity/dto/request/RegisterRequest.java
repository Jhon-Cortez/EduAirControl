package com.eduaircontrol.backend.modules.identity.dto.request;

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
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = ".*[A-Z].*", message = "La contraseña debe contener al menos una mayúscula")
    private String password;
    @NotBlank(message = "El código de empresa es obligatorio")
    @Pattern(regexp = "^[A-Z]{3}-\\d{4}$", message = "Formato de código de empresa inválido")
    private String companyCode;
}
