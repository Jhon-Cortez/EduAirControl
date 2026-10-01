package com.eduaircontrol.backend.modules.identity.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResendCodeRequest {
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Correo inválido")
    private String email;
}
